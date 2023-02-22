package dao

import models.Team

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

class TeamDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CricketTeamTable(tag: Tag) extends Table[Team](tag, "Team") {
    def id = column[Long]("id", O.AutoInc)
    def name = column[String]("name", O.PrimaryKey)
    def * = (id, name) <> ((Team.apply _).tupled, Team.unapply)
  }

  val cricketTeams = TableQuery[CricketTeamTable]

  def create(team: Team): Future[Team] = db.run {
    (cricketTeams.map(_.name)
      returning cricketTeams.map(_.id)
      into ((name, id) => Team(id, name))
      ) += team.name
  }

  def filterQuery(id: Long): Query[CricketTeamTable,Team, Seq] = cricketTeams.filter(_.id === id)

  def filterByNameQuery(name: String): Query[CricketTeamTable,Team, Seq] = cricketTeams.filter(_.name === name)

  def findByName(name: String): Future[Seq[Team]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def getById(id: Long): Future[Option[Team]] = db.run {
    cricketTeams.filter(_.id === id).result.headOption
  }

  def teamsByName(team_name: String): Future[Seq[Team]] = db.run {
    cricketTeams.filter(_.name === team_name).result
  }

  def teamsById(teamId: Long): Future[Seq[Team]] = db.run {
    cricketTeams.filter(_.id === teamId).result
  }

  def list(): Future[Seq[Team]] = db.run {
    cricketTeams.result
  }

  def delete(id: Long): Future[Int] = {
    val pp = db.run(filterQuery(id).delete)
    pp
  }

}