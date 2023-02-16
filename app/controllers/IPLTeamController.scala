package controllers

import dao.IPLTeamDao
import models.IPLTeam

import javax.inject._
import play.api.mvc._
import services.IPLTeamService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class IPLTeamController @Inject()(teamDao: IPLTeamDao, teamService: IPLTeamService, val controllerComponents: ControllerComponents ) extends BaseController {

  def addTeam(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val team = json.asOpt[IPLTeam].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        teamDao.insert(team).map { _ => Ok("Team is added successfully") }
      case None => Future.successful(BadRequest)
    }
  }

  def getScoreByPlayerName(name: String): Action[AnyContent] = Action.async { request =>
    teamDao.findPlayersByName(name).map { data => Ok(s" Players are $data")
    } recover {
      case t: Throwable => BadRequest(s" There is no team by name ${name}")
    }
  }

  def getTeamScore(name: String): Action[AnyContent] = Action.async { request =>
    teamService.findAndAdd(name).map{total => Ok(s"Team score is $total")
    } recover{
      case t: Throwable => BadRequest(s" There is no team by name ${name}")
    }
  }
}
