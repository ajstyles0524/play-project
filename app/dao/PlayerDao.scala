package dao

import models.Player

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

class PlayerDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val cricketTeamDao: TeamDao)
                         (implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketPlayerTable(tag: Tag) extends Table[Player](tag, "PLAYER") {
    def id = column[Long]("id", O.AutoInc)
    def name = column[String]("name", O.PrimaryKey)
    def score = column[Int]("score")
    def team_name = column[String]("team_name")
    def team = foreignKey("team_fk", team_name, cricketTeamDao.cricketTeams)(_.name)
    def * = (id, name, score, team_name) <> ((Player.apply _).tupled, Player.unapply)
  }

  val cricketPlayers = TableQuery[CricketPlayerTable]

  def add(player: Player): Future[Player] = db.run {
    (cricketPlayers.map(p => (p.name, p.score, p.team_name))
      returning cricketPlayers.map(_.id)
      into ((data, id) => Player(id, data._1, data._2, data._3))
      ) += (player.name, player.score, player.team_name)
  }

  def getByTeamId(id: Long): Future[Option[Player]] = db.run {
    cricketPlayers.filter(_.id === id).result.headOption
  }

  def playersByTeam(team_name: String): Future[Seq[Player]] = db.run {
    cricketPlayers.filter(_.team_name === team_name).result
  }


  def list(): Future[Seq[Player]] = db.run {
    cricketPlayers.result
  }

  def filterQuery(id: Long): Query[CricketPlayerTable, Player, Seq] = cricketPlayers.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketPlayerTable,Player, Seq] = cricketPlayers.filter(_.name === name)

  def findByName(name: String): Future[Seq[Player]] = {
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