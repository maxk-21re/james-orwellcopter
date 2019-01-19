package services

import scala.concurrent.Future

import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import javax.inject._
import models.Geolog
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import models.OwntracksMessage
import play.api.Configuration

@Singleton
class MqttConnector @Inject() (
    appLifecycle: ApplicationLifecycle,
    mqttMessageHandler: MqttMessageHandler,
  ){
    mqttMessageHandler.start()
    appLifecycle.addStopHook( () => {
      mqttMessageHandler.stop()
      Future.successful(());
    })
}

@Singleton()
class MqttMessageHandler @Inject()(
    owntracksParser: OwntracksParser,
    config: Configuration
  ){

  val brokerUrl = config.get[String]("orwellcopter.mqtt.server")
  val persistence = new MemoryPersistence
  val client = new MqttClient(this.brokerUrl, MqttClient.generateClientId, this.persistence)

  def start() {
    Logger.info(s"Starting MQTT-Client")
    val topic = "owntracks/+/+"
    val connectionOptions = new MqttConnectOptions()
    connectionOptions.setUserName(config.get[String]("orwellcopter.mqtt.user"))
    connectionOptions.setPassword(config.get[String]("orwellcopter.mqtt.pass").toArray)


    //Connect to MqttBroker
    this.client.connect(connectionOptions)
    //Subscribe to Mqtt topic
    this.client.subscribe(topic)
    //Callback automatically triggers as and when new message arrives on specified topic
    val callback = new MqttCallback {

      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        owntracksParser.parse(topic, message);
      }

      override def connectionLost(cause: Throwable): Unit = {
         println(cause)
       }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {

      }
    }

    //Set up callback for MqttClient
    client.setCallback(callback)

  }

  def stop() {
    Logger.info(s"Stopping MQTT-Client")
    this.client.disconnect()
  }
}
