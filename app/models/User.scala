package models

import play.api.libs.json.Json

case class User(username: String, id: Long = 0L)

object User {
  implicit val userReads  = Json.reads[User]
  implicit val userWrites = Json.writes[User]
}
