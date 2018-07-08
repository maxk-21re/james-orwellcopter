package models

import play.api.libs.json._
import com.vividsolutions.jts.geom.{Envelope}

case class ClusterQueueResponse (mbr: ((Float, Float), (Float, Float)), location: JsValue, adress: JsValue) {
  
  def toCluster(): Cluster =
    new Cluster(new Envelope(mbr._2._2, mbr._1._2, mbr._2._1, mbr._1._1), location, adress)
}

object ClusterQueueResponse {
  implicit val clusterQueueReponseReads = Json.reads[ClusterQueueResponse]
  implicit val clusterQueueReponseWrites = Json.writes[ClusterQueueResponse]
}
