package controllers

import akka.actor.ActorSystem

import javax.inject._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, MessagesActionBuilder, MessagesRequest, Request}

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(controllerComponents: ControllerComponents)(
  implicit executionContext: ExecutionContext,
  messagesAction: MessagesActionBuilder
) extends AbstractController(controllerComponents) {
  var actorSystem = ActorSystem("ActorSystem");

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.index("Welcome to Event management"))
  }

}
