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
  val client = new MqttClient(brokerUrl, MqttClient.generateClientId, new MemoryPersistence)

  val connectionOptions = new MqttConnectOptions()
  connectionOptions.setUserName(config.get[String]("orwellcopter.mqtt.user"))
  connectionOptions.setPassword(config.get[String]("orwellcopter.mqtt.pass").toArray)
  connectionOptions.setAutomaticReconnect(true);

  def start() {
    Logger.info(s"Starting MQTT-Client")
    val topic = "owntracks/+/+"

    mqttConnect()

    //Subscribe to Mqtt topic
    client.subscribe(topic)

    //Callback automatically triggers as and when new message arrives on specified topic
    val callback = new MqttCallbackExtended {

      override def connectComplete(reconnect: Boolean, url: String) {
        Logger.info(s"""${if(reconnect) "Reconnected" else "Connected"} to $url""")
      }

      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        owntracksParser.parse(topic, message);
      }

      override def connectionLost(cause: Throwable): Unit = {
         Logger.error("Lost connection to MQTT-Broker. Attempt to reconnect.", cause)
         mqttConnect()
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      }

    }

    //Set up callback for MqttClient
    client.setCallback(callback)

  }

  def mqttConnect() {
    try {
      client.connect(connectionOptions)
    } catch {
      case e: MqttSecurityException => Logger.error("Connecting failed for security reasons.", e)
      case e: MqttException => Logger.error("Connecting failed.", e)
    }
  }

  def stop() {
    Logger.info(s"Stopping MQTT-Client")
    client.disconnect()
  }
}
