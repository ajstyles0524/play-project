package services

import com.google.inject.Singleton
import dao.{PlayerDao, TeamDao}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TeamService @Inject() (teamDao: TeamDao) {
  def findAndDelete(name: String): Future[Int] ={
    teamDao.findByName(name).flatMap { team =>
      val foundPlayer = team.find(_.name == name)
      foundPlayer match {
        case Some(p) =>
          teamDao.delete(p.id)
        case None => throw new Exception(s"team not found by name ${name}")
      }
    }
  }
}
