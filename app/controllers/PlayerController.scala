package controllers

import dao.PlayerDao
import models.Player

import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.PlayerService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PlayerController @Inject()(playerDao: PlayerDao, playerService: PlayerService, val controllerComponents: ControllerComponents ) extends BaseController {
  val logger = Logger.apply(this.getClass)

  def getPlayerByName(name: String): Action[AnyContent] = Action.async { request =>
    playerDao.findByName(name).map{ players =>
      Ok( Json.toJson(players))
    }recover{
      case t: Throwable =>
        BadRequest(s"Not received player by name ${name}")
    }
  }

  def getPlayer(id: Long): Action[AnyContent] = Action.async { request =>
    playerDao.findById(id).map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(s"Not received player by id ${id}")
    }
  }

  def addPlayer: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val player = json.asOpt[Player].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        playerDao.insert(player).map { p =>
          Ok(Json.toJson(p))
        }
      case None =>
        Future.successful(BadRequest)
    }
  }

  /*def addPlayer: Action[AnyContent] = Action.async { implicit request =>
    println("Called addPlauer")
    playerDao.userForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest(
        "Not good data"+ formWithErrors)
      ),
      player => {
        playerDao.insert(player).map { p =>
          Ok(Json.toJson(p))
        }
      }
    )
  }*/
    /*request.body.asJson match {
      case Some(json) =>
        val player = json.asOpt[Player].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        playerDao.insert(player).map { p =>
          Ok(Json.toJson(p))
        }
      case None =>
        Future.successful(BadRequest)
    }*/


  def updatePlayer: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Player] match {
      case p: JsSuccess[Player] =>
        playerDao.update(p.value).map{ _=>
          Ok("successfully updated player"+ Json.toJson(p.value))
        }
      case error: JsError => Future.successful( BadRequest("error message"+ error))
    }
  }

// Search player by name and delete
  def deletePlayer(name: String): Action[AnyContent] = Action.async { implicit request =>
    playerService.findAndDelete(name).map{
      _ =>  Ok(s"Successfully deleted player ${name}")
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

  def all: Action[AnyContent] = Action.async { implicit request =>
    playerDao.list().map { players =>
      Ok(Json.toJson(players))
    } recover {
      case t: Throwable =>
        BadRequest(t.getLocalizedMessage)
    }
  }

}