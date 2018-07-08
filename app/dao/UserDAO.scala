package dao



import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

@Singleton()
class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  import util.PostgresProfile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
	  def username = column[String]("username")
	  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	  
	  def * = (username, id) <> ((User.apply _).tupled, User.unapply _)
  }
  
  lazy val UserTable = TableQuery[UserTable]
  
  def all(): Future[Seq[User]] = db.run(UserTable.result)
  
  def insert(user: User): Future[Unit] = db.run(UserTable += user).map(_ => ())
  
  def getOrCreateUser(username: String): Future[User] = {
    val getOrCreateQuery  = UserTable.filter{_.username === username}.result.headOption.flatMap {
      case Some(user) => DBIO.successful(user)
      case None => UserTable returning UserTable += User(username)
    }
    db.run(getOrCreateQuery) 
  }
  
}