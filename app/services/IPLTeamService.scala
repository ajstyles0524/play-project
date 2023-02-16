package services

import com.google.inject.Singleton
import dao.IPLPlayerDao
import dao.IPLTeamDao

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


@Singleton
class IPLTeamService @Inject() (teamDao: IPLTeamDao, playerDao: IPLPlayerDao) {

  // working here ...
  val list: List[Int] = List()
  def findAndAdd(name: String): Future[Int] = {
    teamDao.findPlayersByName(name).map { players =>
      players.map(player => playerDao.findScoreByName(player)).map(n => n :: list)
    }
    val num = list.sum
    Future(num)
  }
}
