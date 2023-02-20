package dao

//import models.CricketPlayer
//
//import javax.inject.{Inject, Singleton}
//import play.api.db.slick.DatabaseConfigProvider
//import slick.jdbc.JdbcProfile
//
//import scala.concurrent.{ExecutionContext, Future}
//
//
//@Singleton
//class CricketPlayerDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//  class CricketPlayerTable(tag: Tag) extends Table[CricketPlayer](tag, "cricket_player") {
//
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//    def name = column[String]("name")
//
//    def score = column[Int]("score")
//
//    def * = (id, name, score) <> ((CricketPlayer.apply _).tupled, CricketPlayer.unapply)
//  }
//
//  private val player = TableQuery[CricketPlayerTable]
//
//  def insert(data: CricketPlayer): Future[CricketPlayer] = {
//    db.run(DBIO.seq(player += data)) recover {
//      case t: Throwable =>
//        println("ERROR IS " + t.getLocalizedMessage)
//        throw t
//    } map { _ => data }
//  }
//
//  def update(data: CricketPlayer): Future[Int] = {
//    db.run(player.filter(_.id === data.id).update(data))
//  }
//
//  def filterQuery(id: Long): Query[CricketPlayerTable, CricketPlayer, Seq] = player.filter(_.id === id)
//
//  def filterByNameQuery(name: String): Query[CricketPlayerTable, CricketPlayer, Seq] = player.filter(_.name === name)
//
//  def findByName(name: String): Future[Seq[CricketPlayer]] = {
//    db.run(filterByNameQuery(name).result).map {
//      dataFromDb =>
//        println("Data is >>>>" + dataFromDb)
//        dataFromDb
//    }
//  }
//
//  def delete(id: Long): Future[Int] = {
//    val pp = db.run(filterQuery(id).delete)
//    pp
//  }
//}



import models.CricketPlayer

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class CricketPlayerDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, val cricketTeamDao: CricketTeamDao)
                                 (implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketPlayerTable(tag: Tag) extends Table[CricketPlayer](tag, "cricket_player") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def score = column[Int]("score")
    def teamId = column[Long]("team_id")
    def team = foreignKey("team_fk", teamId, cricketTeamDao.cricketTeams)(_.id)
    def * = (id, name, score, teamId) <> ((CricketPlayer.apply _).tupled, CricketPlayer.unapply)
  }

  val cricketPlayers = TableQuery[CricketPlayerTable]
  //private val cricketTeams = TableQuery[CricketTeam]

  def add(player: CricketPlayer): Future[CricketPlayer] = db.run {
    (cricketPlayers.map(p => (p.name, p.score, p.teamId))
      returning cricketPlayers.map(_.id)
      into ((data, id) => CricketPlayer(id, data._1, data._2, data._3))
      ) += (player.name, player.score, player.teamId)
  }

  def getByTeamId(id: Long): Future[Option[CricketPlayer]] = db.run {
    cricketPlayers.filter(_.id === id).result.headOption
  }

  def playersByTeam(teamId: Long): Future[Seq[CricketPlayer]] = db.run {
    cricketPlayers.filter(_.teamId === teamId).result
  }


  def list(): Future[Seq[CricketPlayer]] = db.run {
    cricketPlayers.result
  }

  def filterQuery(id: Long): Query[CricketPlayerTable, CricketPlayer, Seq] = cricketPlayers.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketPlayerTable, CricketPlayer, Seq] = cricketPlayers.filter(_.name === name)

  def findByName(name: String): Future[Seq[CricketPlayer]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

}