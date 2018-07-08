package dao

import com.vividsolutions.jts.geom.{ Geometry, GeometryFactory }
import java.time.Period
import models.Cluster
import play.api.Logger
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.vividsolutions.jts.geom.Point

import javax.inject.Inject
import models.Geolog
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.time.{LocalDateTime, LocalDate, LocalTime}

class GeologDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import util.PostgresProfile.api._
  
  private val Geologs = TableQuery[GeologTable]
  
  def all(): Future[Seq[Geolog]] = db.run(Geologs.result)
  
  def insert(geolog: Geolog): Future[Unit] = db.run(Geologs += geolog).map(_ => ())
  
  def ofDay(date: LocalDate): Future[Seq[Geolog]] =    
    db.run(Geologs.filter(g => g.timestamp.between(date.atStartOfDay(), date.atTime(LocalTime.MAX))).result)

  def forYesterday(): Future[Seq[Geolog]] = {
    val yesterday = LocalDate.now().minus(Period.ofDays(1))
    ofDay(yesterday)
  }


  def findAndAddToCluster(cluster: Cluster): Future[Seq[Geolog]] = {
    val action = Geologs.filter( g => g.location <@ new GeometryFactory().toGeometry(cluster.mbr) ).result
    db.run(action)
  }

  class GeologTable(tag: Tag) extends Table[Geolog](tag, "geologs") {
    def location = column[Point]("location")
    def accuracy = column[Int]("accuracy")
    def timestamp = column[LocalDateTime]("timestamp")
    def userId = column[Long]("userid")
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    
    def * = (location, accuracy, timestamp, userId, id) <> ((Geolog.apply _).tupled, Geolog.unapply)
  }
}