package dao

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Match

class MatchDao @Inject()(dbConfigProvider: DatabaseConfigProvider, val cricketTeamDao: TeamDao, val tournamentDao: TournamentDao)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MatchTable(tag: Tag) extends Table[Match](tag, "Match") {
    def id = column[Long]("id", O.AutoInc)
    def team_name1 = column[String]("team_name1")
    def team_name2 = column[String]("team_name2")
    def tournament_name = column[String]("tournament_name", O.PrimaryKey)
    def * = (id, team_name1, team_name2, tournament_name) <> ((Match.apply _).tupled, Match.unapply)
    def tournament = foreignKey("tournament_fk", tournament_name, tournamentDao.tournaments)(_.name)
    def team1 = foreignKey("team1_fk", team_name1, cricketTeamDao.cricketTeams)(_.name)
    def team2 = foreignKey("team2_fk", team_name2, cricketTeamDao.cricketTeams)(_.name)
  }

  val matches = TableQuery[MatchTable]

  def insert(matchObj: Match): Future[Match] = db.run {
    (matches.map(m => (m.team_name1, m.team_name2, m.tournament_name))
      returning matches.map(_.id)
      into ((matchData, id) => matchObj.copy(id = id))
      ) += (matchObj.team_name1, matchObj.team_name2, matchObj.tournament_name)
  }


  def list(): Future[Seq[Match]] = db.run {
    matches.result
  }

  def getById(id: Long): Future[Option[Match]] = db.run {
    matches.filter(_.id === id).result.headOption
  }

  def getByTournamentName(tournament_name: String): Future[Seq[Match]] = db.run {
    matches.filter(_.tournament_name === tournament_name).result
  }

  def update(id: Long, tournament_name: String, team_name1: String, team_name2: String): Future[Int] = db.run {
    matches.filter(_.id === id).map(m => (m.tournament_name, m.team_name1, m.team_name2))
      .update(tournament_name, team_name1, team_name2)
  }

  def delete(id: Long): Future[Int] = db.run {
    matches.filter(_.id === id).delete
  }
}

