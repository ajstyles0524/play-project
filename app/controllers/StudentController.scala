package controllers

import dao.StudentDao
import models.Student

import javax.inject._
import play.api._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import services.StudentService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class StudentController @Inject()(studentDao: StudentDao, studentService: StudentService, val controllerComponents: ControllerComponents ) extends BaseController {
  val logger: Logger = Logger.apply(this.getClass)

  def addStudent(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        val student = json.asOpt[Student].getOrElse(throw new NoSuchElementException("Please provide valid data"))
        studentDao.insert(student).map { p => Ok(Json.toJson(p))}
      case None => Future.successful(BadRequest)
    }
  }

  def getStudent(id: Long): Action[AnyContent] = Action.async { request =>
    studentDao.findById(id).map { students =>
      Ok(Json.toJson(students))
    } recover {
      case t: Throwable => BadRequest(s"Not received player by id ${id}")
    }
  }

  def getStudentByName(name: String): Action[AnyContent] = Action.async { request =>
    studentDao.findByName(name).map { students => Ok(Json.toJson(students))
    } recover {
      case t: Throwable => BadRequest(s"Not received player by name ${name}")
    }
  }

  def list_all(): Action[AnyContent] = Action.async { implicit request =>
    studentDao.list().map { students => Ok(Json.toJson(students))
    } recover {
      case t: Throwable => BadRequest(t.getLocalizedMessage)
    }
  }

  def updateStudent(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[Student] match {
      case p: JsSuccess[Student] => studentDao.update(p.value).map { _ => Ok("successfully updated student" + Json.toJson(p.value))}
      case error: JsError => Future.successful(BadRequest("error message" + error))
    }
  }

  def deleteStudent(name: String): Action[AnyContent] = Action.async { implicit request =>
    studentService.findAndDelete(name).map { _ => Ok(s"Successfully deleted player ${name}")
    } recover { case t: Throwable => BadRequest(t.getLocalizedMessage)}
  }
}