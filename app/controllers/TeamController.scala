package controllers
import dao.TeamDao
import models.Team

import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.TeamService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TeamController @Inject()(teamDao: TeamDao, teamService: TeamService, val controllerComponents: ControllerComponents ) extends BaseController {
  val logger = Logger.apply(this.getClass)

  def getTeamByName(name: String): Action[AnyContent] = Action.async { request =>
    teamDao.findByName(name).map{ players =>
      Ok( Json.toJson(players))
    }recover{
      case t: Throwable =>
        BadRequest(s"Not received player by name ${name}")
    }
  }

  def getTeam(id: Long): Action[AnyContent] = Action.async { request =>
    teamDao.findById(id).map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(s"Not received player by id ${id}")
    }
  }

  def addTeam2: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val player = json.asOpt[Team].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        teamDao.insert(player).map { p =>
          Ok(Json.toJson(p))
        }
      case None =>
        Future.successful(BadRequest)
    }
  }

  def addTeam: Action[AnyContent] = Action.async { implicit request =>
    println("Called addPlauer")
    val data = request.body.asFormUrlEncoded
    val teamName = data.get("name").head
    val teamscore = data.get("score").head.toInt
    val team = Team(name= teamName, score = teamscore)
    teamDao.insert(team).map { p =>
      Ok(Json.toJson(p))
    }
  }
  /*request.body.asJson match {
    case Some(json) =>
      val  = json.asOpt[Team].getOrElse(throw new NoSuchElementException("Please provide valid data"))
      teamDao.insert(team).map { p =>
        Ok(Json.toJson(p))
      }
    case None =>
      Future.successful(BadRequest)
  }*/


  def updateTeam: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Team] match {
      case p: JsSuccess[Team] =>
        teamDao.update(p.value).map{ _=>
          Ok("successfully updated team"+ Json.toJson(p.value))
        }
      case error: JsError => Future.successful( BadRequest("error message"+ error))
    }
  }

  // Search team by name and delete
  def deleteTeam(name: String): Action[AnyContent] = Action.async { implicit request =>
    teamService.findAndDelete(name).map{
      _ =>  Ok(s"Successfully deleted team ${name}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def all: Action[AnyContent] = Action.async { implicit request =>
    teamDao.list().map { teams =>
      Ok(Json.toJson(teams))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

}