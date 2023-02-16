package controllers

import dao.IPLTournamentDao
import models.IPLTournament

import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.{StudentService, TournamentService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TournamentController @Inject()(tournamentDao: IPLTournamentDao, tournamentService: TournamentService,  val controllerComponents: ControllerComponents ) extends BaseController {

  def createTournament(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val tournament = json.asOpt[IPLTournament].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        tournamentDao.insert(tournament).map { _ => Ok("Tournament created") }
      case None => Future.successful(BadRequest)
    }
  }

  def list_all(): Action[AnyContent] = Action.async { implicit request =>
    tournamentDao.list().map { tournaments => Ok(Json.toJson(tournaments))
    } recover {
      case t: Throwable => BadRequest(t.getLocalizedMessage)
    }
  }

  def deleteTournament(name: String): Action[AnyContent] = Action.async { implicit request =>
    tournamentService.findAndDelete(name).map { _ => Ok(s"Successfully deleted tournament ${name}")
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage) }
  }

}