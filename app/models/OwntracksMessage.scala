package models
import play.api.libs.json._

case class OwntracksMessage (
  _type: String,
  tid: String,
  acc: Int,
  batt: Int,
  conn: String,
  lat: Float,
  lon: Float,
  tst: Long,
  _cp: Option[Boolean] = None
)

object OwntracksMessage {
  implicit val mqttMessageReads = Json.reads[OwntracksMessage]
  implicit val mqttMessageWrites = Json.writes[OwntracksMessage]
}

case class OwntracksUserInfo (
  username: String,
  deviceId: String
)