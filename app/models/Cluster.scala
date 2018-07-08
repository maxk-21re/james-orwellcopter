package models

import com.vividsolutions.jts.geom.{Envelope}
import play.api.libs.json._
import com.vividsolutions.jts.geom.GeometryFactory
import play.api.libs.functional.syntax._

case class Cluster (mbr: Envelope, location: JsValue, adress: JsValue, id: Long = 0L)

object Cluster {
  implicit val envelopeWrites = new Writes[Envelope] {
    def writes(envelope: Envelope) = Json.obj(
      "minX" -> envelope.getMinX,
      "minY" -> envelope.getMinY,
      "maxX" -> envelope.getMaxX,
      "maxY" -> envelope.getMaxY,
    )
  }

  implicit val pointReads: Reads[Envelope] = (
    (JsPath \ "minX").read[Double] and 
    (JsPath \ "minY").read[Double] and 
    (JsPath \ "maxX").read[Double] and 
    (JsPath \ "maxY").read[Double] 
  )((minX, minY, maxX, maxY) => new Envelope(minX, minY, maxX, maxY) )

  implicit val clusterFormat = Json.format[Cluster]
}
