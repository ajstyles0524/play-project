package dao

//import models.{CricketTeam, Match}
//
//import javax.inject.{Inject, Singleton}
//import play.api.db.slick.DatabaseConfigProvider
//import slick.jdbc.JdbcProfile
//
//import scala.concurrent.duration.Duration
//import scala.language.implicitConversions
//import scala.concurrent.{Await, ExecutionContext, Future}
//
//
//@Singleton
//class MatchDao @Inject()(dbConfigProvider: DatabaseConfigProvider,cricketTeamDao: CricketTeamDao)(implicit ec: ExecutionContext) {
//
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//
//  class MatchTable(tag: Tag) extends Table[Match](tag, "match") {
//
//    implicit val cricketTeamMapper: BaseColumnType[CricketTeam] =
//      MappedColumnType.base[CricketTeam, Long](_.id, id => Await.result(cricketTeamDao.findById(id), Duration.Inf).get)
//
//
//    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//    def team1 = column[CricketTeam]("team1")
//
//    def team2 = column[CricketTeam]("team2")
//
//    def * = (id, team1, team2) <> ((Match.apply _).tupled, Match.unapply)
//
//  }
//
//  val matchTable = TableQuery[MatchTable]
//
//  def insert(data: Match): Future[Match] = {
//    db.run(DBIO.seq(matchTable += data)) recover {
//      case t: Throwable =>
//        println("ERROR IS " + t.getLocalizedMessage)
//        throw t
//    } map { _ => data }
//  }
//
//  def update(data: Match): Future[Int] = {
//    db.run(matchTable.filter(_.id === data.id).update(data))
//  }
//
//  def list(): Future[Seq[Match]] = db.run {
//    matchTable.result
//  }
//
//  def filterQuery(id: Long): Query[MatchTable, Match, Seq] = matchTable.filter(_.id === id)
//
//  def findById(id: Long): Future[Option[Match]] = {
//    db.run(filterQuery(id).result.headOption)
//  }
//
//  def delete(id: Long): Future[Int] = {
//    val pp = db.run(filterQuery(id).delete)
//    pp
//  }
//}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Match

class MatchDao @Inject()(dbConfigProvider: DatabaseConfigProvider, val cricketTeamDao: CricketTeamDao, val tournamentDao: TournamentDao)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class MatchTable(tag: Tag) extends Table[Match](tag, "match") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def team1_id = column[Long]("team_id1")
    def team2_id = column[Long]("team_id2")
    def tournament_id = column[Long]("tournament_id")
    def * = (id, team1_id, team2_id, tournament_id) <> ((Match.apply _).tupled, Match.unapply)
    def tournament = foreignKey("tournament_fk", tournament_id, tournamentDao.tournaments)(_.id)
    def team1 = foreignKey("team1_fk", team1_id, cricketTeamDao.cricketTeams)(_.id)
    def team2 = foreignKey("team2_fk", team2_id, cricketTeamDao.cricketTeams)(_.id)
  }

  val matches = TableQuery[MatchTable]

  def insert(matchObj: Match): Future[Match] = db.run {
    (matches.map(m => (m.team1_id, m.team2_id, m.tournament_id))
      returning matches.map(_.id)
      into ((matchData, id) => matchObj.copy(id = id))
      ) += (matchObj.team_id1, matchObj.team_id2, matchObj.tournamentId)
  }


  def list(): Future[Seq[Match]] = db.run {
    matches.result
  }

  def getById(id: Long): Future[Option[Match]] = db.run {
    matches.filter(_.id === id).result.headOption
  }

  def getByTournamentId(tournamentId: Long): Future[Seq[Match]] = db.run {
    matches.filter(_.tournament_id === tournamentId).result
  }

  def update(id: Long, tournamentId: Long, team1Id: Long, team2Id: Long): Future[Int] = db.run {
    matches.filter(_.id === id).map(m => (m.tournament_id, m.team1_id, m.team2_id))
      .update(tournamentId, team1Id, team2Id)
  }

  def delete(id: Long): Future[Int] = db.run {
    matches.filter(_.id === id).delete
  }
}

