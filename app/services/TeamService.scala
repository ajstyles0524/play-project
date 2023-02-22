package services

import com.google.inject.Singleton
import dao.{PlayerDao, TeamDao, MatchDao}
import models.{Player, Team, Match}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TeamService @Inject()(teamDao: TeamDao, matchDao: MatchDao, cricketPlayerDao: PlayerDao) {

  val matchesByTournament: String => Future[Seq[Match]] = (tournament_name: String) => matchDao.getByTournamentName(tournament_name)
  val playersByTeam: String => Future[Seq[Player]] = (team_name: String) => cricketPlayerDao.playersByTeam(team_name)
  val teamById: Long => Future[Seq[Team]] = (id: Long) => teamDao.teamsById(id)
  val teamByName: String => Future[Seq[Team]] = (team_name: String) => teamDao.teamsByName(team_name)


  def findAndDelete(name: String): Future[Int] ={
    teamDao.findByName(name).flatMap { players =>
      val foundPlayer = players.find(_.name == name)
      foundPlayer match {
        case Some(p) =>
          teamDao.delete(p.id)
        case None => throw new Exception(s"player not found by name ${name}")
      }
    }
  }

  // Which team has max score in tournament
  def getTeamWithMaxScoreByTournament(tournament_name: String): Future[(Team, Int)] = {
    val matchesQuery = matchesByTournament(tournament_name)
    val query = for {
      matches <- matchesQuery
      teamScores <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_name1)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_name2)
          team2Score = team2Players.map(_.score).sum
        } yield {
          if (team1Score > team2Score) (matchValue.team_name1, team1Score)
          else (matchValue.team_name2, team2Score)
        }
      })
      maxScore1 = teamScores.maxBy(_._2)
      maxScore = maxScore1._2
      maxScoreTeamId = maxScore1._1
      maxScoreTeam <- teamByName(maxScoreTeamId)
    }
    yield (maxScoreTeam.head, maxScore)
    query
  }
}