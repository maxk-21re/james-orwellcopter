package dao

import com.vividsolutions.jts.geom.Point
import play.api.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory

import javax.inject.Inject
import javax.inject.Singleton
import models.Cluster
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.JsValue
import scala.util.{Failure, Success}
import slick.jdbc.JdbcProfile

@Singleton()
class ClusterDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit executionContext: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile] {
  import util.PostgresProfile.api._

  case class RawCluster(mbr: Geometry, location: JsValue, adress: JsValue, id: Long = 0L)

  private val Clusters = TableQuery[ClusterTable]

  def all(): Future[Seq[Cluster]] = db.run(Clusters.result).map(f => toCluster(f))

  def find(id: Long): Future[Option[Cluster]] =
    db.run(Clusters.filter(c => c.id === id).result.headOption).map { option =>
      option match {
        case Some(rawCluster) => Some(toCluster(rawCluster))
        case None             => None
      }
    }

  def find(ids: Seq[Long]): Future[Seq[Cluster]] =
    db.run(Clusters.filter(c => c.id inSet ids).result).map { toCluster }

  def clusterForPoint(point: Point): Future[Option[Cluster]] = {
    val action = Clusters.filter(_.mbr contains point).result.headOption
    db.run(action).map { option =>
      option match {
        case Some(rawCluster) => Some(toCluster(rawCluster))
        case None             => None
      }
    }
  }

  def insert(cluster: Cluster): Future[Long] =
    db.run((Clusters returning Clusters.map(_.id)) += toRawCluster(cluster))
  def insert(clusters: Seq[Cluster]): Future[Seq[Long]] =
    db.run((Clusters returning Clusters.map(_.id)) ++= toRawCluster(clusters))

  def toCluster(raw: RawCluster): Cluster =
    new Cluster(raw.mbr.getEnvelopeInternal(), raw.location, raw.adress, raw.id)
  def toCluster(raw: Seq[RawCluster]): Seq[Cluster] =
    raw.map(e => new Cluster(e.mbr.getEnvelopeInternal, e.adress, e.adress, e.id))

  def toRawCluster(cluster: Cluster): RawCluster =
    new RawCluster(new GeometryFactory().toGeometry(cluster.mbr),
                   cluster.location,
                   cluster.adress,
                   cluster.id)
  def toRawCluster(clusters: Seq[Cluster]): Seq[RawCluster] =
    clusters.map(
      cluster =>
        new RawCluster(new GeometryFactory().toGeometry(cluster.mbr),
                       cluster.location,
                       cluster.adress,
                       cluster.id))

  class ClusterTable(tag: Tag) extends Table[RawCluster](tag, "clusters") {
    def mbr      = column[Geometry]("mbr")
    def location = column[JsValue]("location")
    def adress   = column[JsValue]("adress")
    def id       = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def * = (mbr, location, adress, id) <> ((RawCluster.apply _).tupled, RawCluster.unapply _)
  }
}
