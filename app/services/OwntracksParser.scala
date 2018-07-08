package services

import dao.ClusterDAO
import org.eclipse.paho.client.mqttv3.MqttMessage

import dao.UserDAO
import javax.inject.{Inject, Singleton}
import models.OwntracksMessage
import models.OwntracksUserInfo
import play.api.libs.json.JsError
import play.api.libs.json.JsSuccess
import play.api.libs.json.Json
import models.User
import scala.util.Success
import scala.util.Failure
import scala.concurrent.ExecutionContext
import dao.GeologDAO
import models.Geolog
import java.sql.Date
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.PrecisionModel
import com.vividsolutions.jts.geom.Point
import java.sql.Timestamp
import com.vividsolutions.jts.geom.GeometryFactory
import java.time.ZoneId
import java.time.LocalDateTime
import java.time.Instant
import play.api.Logger

@Singleton()
class OwntracksParser @Inject()(userDao: UserDAO, geologDao: GeologDAO, clusterDao: ClusterDAO)(
    implicit ec: ExecutionContext) {
  def parse(topic: String, payload: MqttMessage) {

    val e = for {
      owntracksMessage <- validateOwntracksMessage(payload.toString())
      userInfo         <- extractUserInfoFromTopic(topic)
    } yield (owntracksMessage, userInfo)

    e match {
      case Some((om, userinfo)) => {
        for {
          user <- userDao.getOrCreateUser(userinfo.username)
          cluster <- clusterDao.clusterForPoint(
            new GeometryFactory(new PrecisionModel(), 0)
              .createPoint(new Coordinate(om.lat, om.lon)))
          geolog = geologDao
            .insert(
              Geolog(
                new GeometryFactory(new PrecisionModel(), 0)
                  .createPoint(new Coordinate(om.lat, om.lon)),
                om.acc,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(om.tst * 1000),
                                        ZoneId.of("Europe/Berlin")),
                user.id,
                cluster match {
                  case Some(c) => Some(c.id)
                  case None    => None
                }
              ))
            .onComplete {
              case Failure(t) => println(t.getMessage)
              case _          => Logger.info("Added new Geolog")
            }
        } yield geolog
      }
      case None => Logger.warn("Could not unpack Message")
    }
  }

  def validateOwntracksMessage(payload: String): Option[OwntracksMessage] = {
    val json        = Json.parse(payload.toString())
    val maybeGeolog = json.validate[OwntracksMessage]
    maybeGeolog match {
      case s: JsSuccess[OwntracksMessage] => Some(s.get)
      case e: JsError => {
        Logger.warn("Unexptected Log-Format. Exception: " + e.errors.mkString(" | "))
        None
      }
    }
  }

  def extractUserInfoFromTopic(topicString: String): Option[OwntracksUserInfo] = {
    val extractPattern = raw"owntracks\/(\w*)\/(\w*)".r
    topicString match {
      case extractPattern(user, deviceId) => Some(OwntracksUserInfo(user, deviceId))
      case _                              => None
    }
  }

}
