package services

import com.google.inject.Singleton
import dao.IPLTournamentDao

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class TournamentService @Inject() (tournamentDao: IPLTournamentDao) {
  def findAndDelete(name: String): Future[Int] = {
    tournamentDao.findByName(name).flatMap { tournaments =>
      val foundTournament = tournaments.find(_.name == name)
      foundTournament match {
        case Some(p) => tournamentDao.delete(p.id)
        case None => throw new Exception(s"tournament not found by name ${name}")
      }
    }
  }
}
