package services

import com.google.inject.Singleton
import dao.{PlayerDao, TeamDao, MatchDao, TournamentDao}
import models.{Player, Team, Match}
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TournamentService @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, tournamentDao: TournamentDao, matchDao: MatchDao, teamDao: TeamDao, cricketPlayerDao: PlayerDao) {


  // define the queries
  val matchesByTournament: String => Future[Seq[Match]] = (tournament_name: String) => matchDao.getByTournamentName(tournament_name)
  val playersByTeam: String => Future[Seq[Player]] = (team_name: String) => cricketPlayerDao.playersByTeam(team_name)
  val teamById: Long => Future[Seq[Team]] = (id: Long) => teamDao.teamsById(id)
  val teamByName: String => Future[Seq[Team]] = (team_name: String) => teamDao.teamsByName(team_name)


  def findAndDelete(name: String): Future[Int] = {
    tournamentDao.findByName(name).flatMap { tournaments =>
      val foundTournament = tournaments.find(_.name == name)
      foundTournament match {
        case Some(p) => tournamentDao.delete(p.id)
        case None => throw new Exception(s"tournament not found by name ${name}")
      }
    }
  }

  def getWinningTeamByTournaments(tournament_name: String): Future[Seq[Team]] = {
    val matchesQuery = matchesByTournament(tournament_name)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_name1)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_name2)
          team2Score = team2Players.map(_.score).sum
          winningTeamName = if (team1Score > team2Score) matchValue.team_name1 else matchValue.team_name2
          winningTeam <- teamByName(winningTeamName)
        } yield winningTeam
      })
    } yield winningTeams.flatten
    query
  }

  def getWinningTeamWithMaxScorer(tournament_name: String): Future[Seq[(String, Player)]] = {
    val matchesQuery = matchesByTournament(tournament_name)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_name1)
          team1MaxScorer = team1Players.maxBy(_.score)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_name2)
          team2MaxScorer = team2Players.maxBy(_.score)
          team2Score = team2Players.map(_.score).sum
          value = if (team1Score > team2Score) (matchValue.team_name1, team1MaxScorer) else (matchValue.team_name2, team2MaxScorer)
        } yield value
      })
    } yield winningTeams
    query
  }
}
