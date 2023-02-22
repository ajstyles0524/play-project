package controllers

import dao.MatchDao
import models.Match
import play.api.libs.json.Json

import javax.inject._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MatchController @Inject()(val matchDao: MatchDao, val controllerComponents: ControllerComponents ) extends BaseController {

  def addMatch(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val value = json.asOpt[Match].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        matchDao.insert(value).map { data => Ok(Json.toJson(data)) }
      case None => Future.successful(BadRequest)
    }
  }

  def all(): Action[AnyContent] = Action.async { implicit request =>
    matchDao.list().map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def deleteMatch(id: Long): Action[AnyContent] = Action.async { implicit request =>
    matchDao.delete(id).map {
      _ => Ok(s"Successfully deleted match ${id}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

}
