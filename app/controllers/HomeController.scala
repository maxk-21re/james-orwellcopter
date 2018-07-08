package controllers

import dao.GeologDAO
import javax.inject._
import play.api.mvc._
import play.api.libs.concurrent.Futures._
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDate
import services.ClusterService
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, geologDao: GeologDAO, clusterService: ClusterService) (implicit assetsFinder: AssetsFinder)
  extends AbstractController(cc) {
  
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {
      geologDao.forYesterday().map { log => Ok(Json.toJson(log))}
  }

  def listPlaces = Action {
    Ok("lol")
  }
  
  def cluster = Action.async { request =>
      geologDao.forYesterday().map { log => Ok(Json.toJson(log))}
  }

}
