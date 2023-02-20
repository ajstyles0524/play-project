package services

import com.google.inject.Singleton
import dao.{CricketPlayerDao, CricketTeamDao, MatchDao}
import models.{CricketPlayer, CricketTeam, Match}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class CricketPlayerService @Inject() (playerDao: CricketPlayerDao,matchDao: MatchDao, cricketPlayerDao: CricketPlayerDao,cricketTeamDao: CricketTeamDao) {
  val matchesByTournament: Long => Future[Seq[Match]] = (tournamentId: Long) => matchDao.getByTournamentId(tournamentId)
  val playersByTeam: Long => Future[Seq[CricketPlayer]] = (teamId: Long) => cricketPlayerDao.playersByTeam(teamId)
  val teamById: Long => Future[Seq[CricketTeam]] = (id: Long) => cricketTeamDao.teamsById(id)


  def findAndDelete(name: String): Future[Int] ={
    playerDao.findByName(name).flatMap { players =>
      val foundPlayer = players.find(_.name == name)
      foundPlayer match {
        case Some(p) =>
          playerDao.delete(p.id)
        case None => throw new Exception(s"player not found by name ${name}")
      }
    }
  }

  def getMaxScorerInTournament(tournamentId: Long): Future[(CricketPlayer, Int)] = {
    val matchesQuery = matchesByTournament(tournamentId)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_id1)
          team1MaxScorer = team1Players.maxBy(_.score)
          maxScoreByTeam1Player = team1MaxScorer.score
          team2Players <- playersByTeam(matchValue.team_id2)
          team2MaxScorer = team2Players.maxBy(_.score)
          maxScoreByTeam2Player = team2MaxScorer.score
          maxScorerOfMatch = if(maxScoreByTeam1Player > maxScoreByTeam2Player) team1MaxScorer else team2MaxScorer
        } yield (maxScorerOfMatch, maxScorerOfMatch.score)
      })
    } yield winningTeams.maxBy(_._2)
    query
  }



}