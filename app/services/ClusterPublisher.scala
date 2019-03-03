package services

import dao.{ ClusterDAO, GeologDAO }
import org.joda.time.LocalDateTime
import scala.concurrent.Future

import akka.actor.{Actor, ActorSystem, Props}
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.inject.ApplicationLifecycle

@Singleton
class ClusterPublisherService @Inject()(
    appLifecycle: ApplicationLifecycle,
    clusterPublishScheduler: ClusterPublishScheduler
  ) {

  clusterPublishScheduler.start
  
  appLifecycle.addStopHook( () => {
    clusterPublishScheduler.stop
    Future.successful(());
  })
}

@Singleton
class ClusterPublishScheduler @Inject()(
  clusterService: ClusterService,
  geologDao: GeologDAO,
    ) {
  import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
  import com.typesafe.config.ConfigFactory

  case object Tick

  val schedulerExpression = """akka.quartz.schedules.At1AM.expression = "0 12 23 * * ? *""""
  val scheduleActor = ActorSystem("scheduler", ConfigFactory.parseString(schedulerExpression))
  def start = {
    Logger.info("Starting ClusterPublishScheduler: " + new LocalDateTime().toString())

    class ClusterPublishActor extends Actor {
      import scala.concurrent.ExecutionContext.Implicits.global

      def receive = {
        case _ => {
          Logger.info("Queue yesterdays Geologs for clustering")
          geologDao.forYesterday().map { cluster => clusterService.calculate(cluster) }
        }
      }
    }

    object ClusterPublishActor {
      def props(): Props = Props(new ClusterPublishActor)
    }

    val scheduler = QuartzSchedulerExtension(scheduleActor).schedule("At1AM", scheduleActor.actorOf(ClusterPublishActor.props()), Tick)
    Logger.info("First publish scheduled for " + scheduler.toString())
  }

  def stop = {
    Logger.info("Stopping ClusterPublishScheduler")
    scheduleActor.terminate()
  }
}
