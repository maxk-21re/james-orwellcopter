package services

import com.newmotion.akka.rabbitmq.BasicProperties
import com.newmotion.akka.rabbitmq.Channel
import com.newmotion.akka.rabbitmq.ChannelActor
import com.newmotion.akka.rabbitmq.ChannelMessage
import com.newmotion.akka.rabbitmq.ConnectionActor
import com.newmotion.akka.rabbitmq.ConnectionFactory
import com.newmotion.akka.rabbitmq.DefaultConsumer
import com.newmotion.akka.rabbitmq.Envelope
import com.newmotion.akka.rabbitmq.RichConnectionActor

import akka.actor.ActorRef
import akka.actor.ActorSystem
import dao.GeologDAO
import javax.inject.Singleton
import models.ClusterQueueResponse
import models.Geolog
import play.api.Logger
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import dao.ClusterDAO
import javax.inject.Inject
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext
import play.api.Configuration

@Singleton
class ClusterService @Inject()(
    clusterDao: ClusterDAO,
    geologDao: GeologDAO,
    config: Configuration
)(implicit ec: ExecutionContext) {

  implicit val system = ActorSystem()
  val factory         = new ConnectionFactory()
  factory.setHost(config.get[String]("orwellcopter.amqp.server"))
  factory.setUsername(config.get[String]("orwellcopter.amqp.user"))
  factory.setPassword(config.get[String]("orwellcopter.amqp.pass"))

  val connection   = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
  val exchangeName = "CLUSTER"

  def setupPublisher(channel: Channel, self: ActorRef) {
    channel.exchangeDeclare(exchangeName, "topic")
    val queue = channel.queueDeclare("CALC_CLUSTER_START", false, false, false, null).getQueue
    channel.queueBind(queue, exchangeName, "CALC_CLUSTER_START")
  }
  val publishActor: ActorRef =
    connection.createChannel(ChannelActor.props(setupPublisher), Some("publisher"))

  def setupSubscriber(channel: Channel, self: ActorRef) {
    channel.exchangeDeclare(exchangeName, "topic")
    val queue = channel.queueDeclare("CALC_CLUSTER_FINISHED", false, false, false, null).getQueue
    channel.queueBind(queue, exchangeName, "CALC_CLUSTER_FINISHED")

    val consumer = new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String,
                                  envelope: Envelope,
                                  properties: BasicProperties,
                                  body: Array[Byte]) {
        Json.parse(fromBytes(body)).validate[Seq[ClusterQueueResponse]] match {
          case s: JsSuccess[Seq[ClusterQueueResponse]] => {
            println("Received Cluster:" + fromBytes(body))
            val parsedClusters = s.get.map(f => f.toCluster())
            clusterDao.insert(parsedClusters) onComplete {
              case Failure(t) => println(t.getStackTrace)
              case Success(result) => {
                clusterDao.find(result).onComplete {
                  case Success(clusterSeq) => clusterSeq.map { geologDao.findAndAddToCluster }
                  case Failure(t)          => Logger.error(t.toString())
                }
              }
            }
          }
          case e: JsError => {
            println("Unexptected Queue-Response. Exception: " + e.errors.mkString(" | "))
          }
        }
      }
    }

    channel.basicConsume(queue, true, consumer)
  }
  val subscribeActor: ActorRef =
    connection.createChannel(ChannelActor.props(setupSubscriber), Some("subscriber"))

  def calculate(logs: Seq[Geolog]): Unit = {
    val payload = Json.toJson(logs)

    def publish(channel: Channel) {
      channel.basicPublish(exchangeName,
                           "CALC_CLUSTER_START",
                           null,
                           payload.toString().getBytes("UTF-8"))
    }
    publishActor ! ChannelMessage(publish, dropIfNoChannel = false)
  }

  def fromBytes(x: Array[Byte]) = new String(x, "UTF-8")

}
