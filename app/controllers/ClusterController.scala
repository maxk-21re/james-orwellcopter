package controllers

import dao.{ClusterDAO, GeologDAO}
import javax.inject._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import play.api.libs.concurrent.Futures._
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDate
import scala.util.{Failure, Success}
import services.ClusterService
import scala.concurrent.Future
import models.Cluster

@Singleton
class ClusterController @Inject()(cc: ControllerComponents,
                                  clusterDao: ClusterDAO,
                                  geologDAO: GeologDAO)(implicit assetsFinder: AssetsFinder)
    extends AbstractController(cc) {

  def cluster(id: Long) = Action.async { request =>
    clusterDao.find(id).map {
      case Some(cluster) => Ok(Json.toJson(cluster))
      case None          => NotFound("Not found")
    }
  }

  def clusterLogs(id: Long) = Action.async {
    clusterDao.find(id).flatMap {
      case Some(cluster) => {
        geologDAO.findAndAddToCluster(cluster).map { logs =>
          Ok(Json.toJson(logs))
        }
      }
      case None => Future { NotFound("Cluster not found") }
    }
  }
}
