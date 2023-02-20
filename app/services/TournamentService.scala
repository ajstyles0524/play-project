package services

import com.google.inject.Singleton
import dao.{CricketPlayerDao, CricketTeamDao, MatchDao, TournamentDao}
import models.{CricketPlayer, CricketTeam, Match}
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TournamentService @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, tournamentDao: TournamentDao, matchDao: MatchDao, cricketTeamDao: CricketTeamDao, cricketPlayerDao: CricketPlayerDao) {


  // define the queries
  val matchesByTournament: Long => Future[Seq[Match]] = (tournamentId: Long) => matchDao.getByTournamentId(tournamentId)
  val playersByTeam: Long => Future[Seq[CricketPlayer]] = (teamId: Long) => cricketPlayerDao.playersByTeam(teamId)
  val teamById: Long => Future[Seq[CricketTeam]]  = (id: Long) => cricketTeamDao.teamsById(id)
  val getTeamById: Long => Future[Option[CricketTeam]] = (id: Long) => cricketTeamDao.getById(id)

  def findAndDelete(name: String): Future[Int] = {
    tournamentDao.findByName(name).flatMap { tournaments =>
      val foundTournament = tournaments.find(_.name == name)
      foundTournament match {
        case Some(p) => tournamentDao.delete(p.id)
        case None => throw new Exception(s"tournament not found by name ${name}")
      }
    }
  }

  def getWinningTeamByTournaments(tournamentId: Long): Future[Seq[CricketTeam]] = {
    val matchesQuery = matchesByTournament(tournamentId)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_id1)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_id2)
          team2Score = team2Players.map(_.score).sum
          winningTeamId = if (team1Score > team2Score) matchValue.team_id1 else matchValue.team_id2
          winningTeam <- teamById(winningTeamId)
        } yield winningTeam
      })
    } yield winningTeams.flatten
    query
  }

  def getWinningTeamWithMaxScorer(tournamentId: Long): Future[Seq[(Long, CricketPlayer)]] = {
    val matchesQuery = matchesByTournament(tournamentId)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_id1)
          team1MaxScorer = team1Players.maxBy(_.score)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_id2)
          team2MaxScorer = team2Players.maxBy(_.score)
          team2Score = team2Players.map(_.score).sum
          value = if (team1Score > team2Score) (matchValue.team_id1, team1MaxScorer) else (matchValue.team_id2, team2MaxScorer)
        } yield value
      })
    } yield winningTeams
    query
  }
}
