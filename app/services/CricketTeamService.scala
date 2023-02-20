package services

import com.google.inject.Singleton
import dao.{CricketPlayerDao, CricketTeamDao, MatchDao, TournamentDao}
import models.{CricketPlayer, CricketTeam, Match}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class CricketTeamService @Inject() (teamDao: CricketTeamDao, tournamentDao: TournamentDao, matchDao: MatchDao, cricketPlayerDao: CricketPlayerDao) {

  val matchesByTournament: Long => Future[Seq[Match]] = (tournamentId: Long) => matchDao.getByTournamentId(tournamentId)
  val playersByTeam: Long => Future[Seq[CricketPlayer]] = (teamId: Long) => cricketPlayerDao.playersByTeam(teamId)
  val teamById: Long => Future[Seq[CricketTeam]] = (id: Long) => teamDao.teamsById(id)


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
  def getTeamWithMaxScoreByTournament(tournamentId: Long): Future[(CricketTeam, Int)] = {
    val matchesQuery = matchesByTournament(tournamentId)
    val query = for {
      matches <- matchesQuery
      teamScores <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_id1)
          team1Score = team1Players.map(_.score).sum
          team2Players <- playersByTeam(matchValue.team_id2)
          team2Score = team2Players.map(_.score).sum
        } yield {
          if (team1Score > team2Score) (matchValue.team_id1, team1Score)
          else (matchValue.team_id2, team2Score)
        }
      })
      maxScore1 = teamScores.maxBy(_._2)
      maxScore = maxScore1._2
      maxScoreTeamId = maxScore1._1
      maxScoreTeam <- teamById(maxScoreTeamId)
    }
    yield (maxScoreTeam.head, maxScore)
    query
  }

}