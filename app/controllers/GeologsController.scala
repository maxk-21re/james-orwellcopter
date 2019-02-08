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
class GeologsController @Inject()(cc: ControllerComponents, geologDAO: GeologDAO)(
    implicit assetsFinder: AssetsFinder)
    extends AbstractController(cc) {

  def log(id: Long) = Action.async { request =>
    geologDAO.find(id).map {
      case Some(cluster) => Ok(Json.toJson(cluster))
      case None          => NotFound("Not found")
    }
  }

  def forRange(startDateRaw: String, endDateRaw: String) = Action.async { request =>
    geologDAO
      .between(LocalDate.parse(startDateRaw), LocalDate.parse(endDateRaw))
      .map(i => Ok(Json.toJson(i)))
  }
}
