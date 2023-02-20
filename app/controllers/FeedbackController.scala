//package controllers
//
//import dao.FeedbackDao
//import models.FeedbackForm
//import play.api.data.Forms._
//import play.api.data._
//import play.api.mvc._
//import javax.inject._
//import views.html.
//
//
//
//@Singleton
//class FeedbackController @Inject()(val controllerComponents: ControllerComponents, feedbackDao: FeedbackDao) extends BaseController {
//
//  val feedbackForm = Form(
//    mapping(
//      "id" -> number,
//      "name" -> nonEmptyText,
//      "email" -> email,
//      "rating" -> number(min = 1, max = 5),
//      "comments" -> nonEmptyText
//    )(FeedbackForm.apply)(FeedbackForm.unapply)
//  )
//
//  def index() = Action { implicit request: Request[AnyContent] =>
//    val feedbacks = feedbackDao.getAll()
//    Ok(views.html.form)
//  }
//
//  def feedbackForm() = Action { implicit request: Request[AnyContent] =>
//    Ok(views.html.feedbackForm(feedbackForm))
//  }
//
//  def add() = Action { implicit request: Request[AnyContent] =>
//    feedbackForm.bindFromRequest().fold(
//      formWithErrors => {
//        BadRequest(views.html.feedbackForm(formWithErrors))
//      },
//      feedback => {
//        feedbackDao.add(feedback)
//        Redirect(routes.FeedbackController.index())
//      }
//    )
//  }
//}
