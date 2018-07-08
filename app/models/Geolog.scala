package models

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, Instant, ZoneId}

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.Point

import play.api.libs.functional.syntax._
import play.api.libs.json._
import java.time.format.DateTimeFormatter

case class Geolog(location: Point,
                  accuracy: Int,
                  timestamp: LocalDateTime,
                  userId: Long,
                  clusterId: Option[Long] = None,
                  id: Long = 0L)

object Geolog {
  implicit object timestampFormat extends Format[LocalDateTime] {
    val format    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")

    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(
        LocalDateTime.ofInstant(Instant.ofEpochMilli(format.parse(str).getTime),
                                ZoneId.of("Europe/Berlin")))
    }

    def writes(ts: LocalDateTime) =
      JsString(ts.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")))
  }

  implicit val pointWrites = new Writes[Point] {
    def writes(point: Point) = Json.obj(
      "latitude"  -> point.getX,
      "longitude" -> point.getY
    )
  }

  implicit val pointReads: Reads[Point] = (
    (__ \ "latitude").read[Float] and
    (__ \ "longitude").read[Float]
  )((latitude, longitude) => new GeometryFactory().createPoint(new Coordinate(latitude, longitude)))

  // implicit val geologWrites: Writes[Geolog] = (
  //   (JsPath \ "location").write[Point] and
  //   (JsPath \ "accuracy").write[Int] and
  //   (JsPath \ "timestamp").write[LocalDateTime] and
  //   (JsPath \ "userId").write[Long] and
  //   (JsPath \ "custerId").write[Option[Long]] and
  //   (JsPath \ "id").write[Long]
  // )(unlift(Geolog.unapply))

  // implicit val geologReads: Reads[Geolog] = (
  //   (JsPath \ "location").read[Point] and
  //   (JsPath \ "accuracy").read[Int] and
  //   (JsPath \ "timestamp").read[LocalDateTime]  and
  //   (JsPath \ "userId").read[Long] and
  //   (JsPath \ "clusterId").read[Option[Long]] and
  //   (JsPath \ "id").read[Long]
  // )(Geolog.apply _)

  implicit val geologFormat = Json.format[Geolog]

}
