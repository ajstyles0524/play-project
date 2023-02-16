package controllers

import dao.IPLPlayerDao
import models.IPLPlayer

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class IPLPlayerController @Inject()(playerDao: IPLPlayerDao,  val controllerComponents: ControllerComponents ) extends BaseController {

  def addPlayer(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val player = json.asOpt[IPLPlayer].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        playerDao.insert(player).map { p => Ok("Player is added successfully") }
      case None => Future.successful(BadRequest)
    }
  }

  def getScoreByPlayerName(name: String): Action[AnyContent] = Action.async { request =>
    playerDao.findScoreByName(name).map { data => Ok(s"Score is $data")
    } recover {
      case t: Throwable => BadRequest(s" There is no player by name ${name}")
    }
  }




}