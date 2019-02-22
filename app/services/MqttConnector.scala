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
  val topic = "owntracks/+/+"

  val connectionOptions = new MqttConnectOptions()
  connectionOptions.setUserName(config.get[String]("orwellcopter.mqtt.user"))
  connectionOptions.setPassword(config.get[String]("orwellcopter.mqtt.pass").toArray)
  connectionOptions.setAutomaticReconnect(true);
  val callback = new MqttCallbackExtended {

    override def connectComplete(reconnect: Boolean, url: String) {
      // Hopefully this whole thing will restart listening for messages again
      // after connecting.
      Logger.info(s"""${if(reconnect) "Reconnected" else "Connected"} to $url""")
      client.subscribe(topic)
      client.setCallback(this)
    }

    override def messageArrived(topic: String, message: MqttMessage): Unit = {
      owntracksParser.parse(topic, message);
    }

    override def connectionLost(cause: Throwable): Unit = {
        Logger.error("Lost connection to MQTT-Broker. Attempt to reconnect.", cause)
        start()
    }

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
    }
  }

  def start() {
    Logger.info(s"Starting MQTT-Client")

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
