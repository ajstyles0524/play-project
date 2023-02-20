package dao

import models.FeedbackForm

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FeedbackDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val db = dbConfig.db
  import dbConfig.profile.api._

  private class FeedbackTable(tag: Tag) extends Table[FeedbackForm](tag, "feedback") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def rating = column[Int]("rating")
    def comments = column[String]("comments")

    def * = (id, name, email, rating, comments) <> ((FeedbackForm.apply _).tupled, FeedbackForm.unapply)
  }

  private val feedbacks = TableQuery[FeedbackTable]

  def getAll(): Future[Seq[FeedbackForm]] = db.run(feedbacks.result)

  def add(feedback: FeedbackForm): Future[Unit] = {
    db.run(feedbacks += feedback).map(_ => ())
  }
}
