package models

import com.vividsolutions.jts.geom.{Coordinate, Envelope, LinearRing, Polygon}
import play.api.libs.json._
import com.vividsolutions.jts.geom.GeometryFactory
import play.api.libs.functional.syntax._

case class Fuckery(p: Polygon)
object Fuckery {
  implicit val polygonWrites: Writes[Fuckery] = (
    (JsPath).write[Fuckery]
  )(
    (p: Fuckery) =>
      JsArray(
        p.p.getCoordinates()
          .map { coord =>
            JsArray(Seq(JsNumber(coord.x), JsNumber(coord.y)))
          }
          .toSeq))

  implicit val polygonReads: Reads[Fuckery] = (
    (JsPath).read[Fuckery]
  )((p: JsValue) => {
    p.validate[Seq[(Double, Double)]] match {
      case s: JsSuccess[Seq[(Double, Double)]] =>
        JsSuccess(Fuckery(
          new GeometryFactory().createPolygon(s.get.map(i => new Coordinate(i._1, i._2)).toArray)))
      case e: JsError => {
        println("Failed parsing to Polygon: " + e.errors.mkString(" | "))
        e
      }
    }
  })
}

case class Cluster(mbr: Envelope,
                   shell: Fuckery,
                   location: JsValue,
                   adress: JsValue,
                   id: Long = 0L)

object Cluster {

  implicit val envelopeWrites = new Writes[Envelope] {
    def writes(envelope: Envelope) = Json.obj(
      "minX" -> envelope.getMinX,
      "minY" -> envelope.getMinY,
      "maxX" -> envelope.getMaxX,
      "maxY" -> envelope.getMaxY
    )
  }

  implicit val pointReads: Reads[Envelope] = (
    (JsPath \ "minX").read[Double] and
    (JsPath \ "minY").read[Double] and
    (JsPath \ "maxX").read[Double] and
    (JsPath \ "maxY").read[Double]
  )((minX, minY, maxX, maxY) => new Envelope(minX, minY, maxX, maxY))

  implicit val clusterFormat = Json.format[Cluster]
}
