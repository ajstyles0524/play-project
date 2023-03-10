package controllers

import dao.TournamentDao
import models.Tournament

import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._
import services.TournamentService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TournamentController @Inject()(tournamentDao: TournamentDao, tournamentService: TournamentService, val controllerComponents: ControllerComponents ) extends BaseController {

  def createTournament(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val tournament = json.asOpt[Tournament].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        tournamentDao.add(tournament).map { _ => Ok("Tournament created") }
      case None => Future.successful(BadRequest)
    }
  }

  def all(): Action[AnyContent] = Action.async { implicit request =>
    tournamentDao.list().map { tournaments => Ok(Json.toJson(tournaments))
    } recover {
      case t: Throwable => BadRequest(t.getLocalizedMessage)
    }
  }

  def matchesByTournamentId(id: Long): Action[AnyContent] = Action.async { implicit request =>
    tournamentService.matchesByTournament(id).map { data => Ok(Json.toJson(data))
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage) }
  }

  def getWinnerWithMaxScorer(id: Long): Action[AnyContent] = Action.async { implicit request =>
    tournamentService.getWinningTeamWithMaxScorer(id).map { data => Ok(s"Winners with Team id and Max Scorer details - $data ")
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage) }
  }

  def deleteTournament(name: String): Action[AnyContent] = Action.async { implicit request =>
    tournamentService.findAndDelete(name).map { _ => Ok(s"Successfully deleted tournament ${name}")
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage) }
  }

  def getAllWinnersById(id: Long): Action[AnyContent] = Action.async { implicit request =>
    tournamentService.getWinningTeamByTournaments(id).map { data => Ok(s" Winner Teams are $data")
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage) }
  }

}