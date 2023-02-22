package services

import com.google.inject.Singleton
import dao.{PlayerDao, TeamDao, MatchDao}
import models.{Player, Team, Match}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class PlayerService @Inject()(playerDao: PlayerDao, matchDao: MatchDao, cricketPlayerDao: PlayerDao, teamDao: TeamDao) {

  val matchesByTournament: String => Future[Seq[Match]] = (tournament_name: String) => matchDao.getByTournamentName(tournament_name)
  val playersByTeam: String => Future[Seq[Player]] = (team_name: String) => cricketPlayerDao.playersByTeam(team_name)
  val teamById: Long => Future[Seq[Team]] = (id: Long) => teamDao.teamsById(id)
  val teamByName: String => Future[Seq[Team]] = (team_name: String) => teamDao.teamsByName(team_name)


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

  def getMaxScorerInTournament(tournament_name: String): Future[(Player, Int)] = {
    val matchesQuery = matchesByTournament(tournament_name)
    val query = for {
      matches <- matchesQuery
      winningTeams <- Future.sequence(matches.map { matchValue =>
        for {
          team1Players <- playersByTeam(matchValue.team_name1)
          team1MaxScorer = team1Players.maxBy(_.score)
          maxScoreByTeam1Player = team1MaxScorer.score
          team2Players <- playersByTeam(matchValue.team_name2)
          team2MaxScorer = team2Players.maxBy(_.score)
          maxScoreByTeam2Player = team2MaxScorer.score
          maxScorerOfMatch = if(maxScoreByTeam1Player > maxScoreByTeam2Player) team1MaxScorer else team2MaxScorer
        } yield (maxScorerOfMatch, maxScorerOfMatch.score)
      })
    } yield winningTeams.maxBy(_._2)
    query
  }
}