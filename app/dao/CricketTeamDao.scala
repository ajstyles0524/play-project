package dao

import models.CricketTeam

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

class CricketTeamDao @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketTeamTable(tag: Tag) extends Table[CricketTeam](tag, "cricket_team") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((CricketTeam.apply _).tupled, CricketTeam.unapply)
  }

  val cricketTeams = TableQuery[CricketTeamTable]

  def create(team: CricketTeam): Future[CricketTeam] = db.run {
    (cricketTeams.map(_.name)
      returning cricketTeams.map(_.id)
      into ((name, id) => CricketTeam(id, name))
      ) += team.name
  }

  def filterQuery(id: Long): Query[CricketTeamTable,CricketTeam, Seq] = cricketTeams.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketTeamTable,CricketTeam, Seq] = cricketTeams.filter(_.name === name)

  def findByName(name: String): Future[Seq[CricketTeam]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def getById(id: Long): Future[Option[CricketTeam]] = db.run {
    cricketTeams.filter(_.id === id).result.headOption
  }

  def teamsById(teamId: Long): Future[Seq[CricketTeam]] = db.run {
    cricketTeams.filter(_.id === teamId).result
  }

  def list(): Future[Seq[CricketTeam]] = db.run {
    cricketTeams.result
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

}