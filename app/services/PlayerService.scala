package services

import com.google.inject.Singleton
import dao.PlayerDao

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PlayerService @Inject() (playerDao: PlayerDao) {
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
}
