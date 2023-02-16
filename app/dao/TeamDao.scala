package dao

import models.{Team}
import play.api.data.Form

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  class TeamTable(tag: Tag) extends Table[Team](tag, "team") {
    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    /** The name column */
    def name = column[String]("name")
    /** The age column */
    def score = column[Int]("score")

    def * = (id, name, score) <> ((Team.apply _).tupled, Team.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val team = TableQuery[TeamTable]

  /**
   * Create a person with the given name and age.
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def insert(data: Team): Future[Team] = {
    db.run(
      DBIO.seq(
        team += data
      )
    ) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { d =>
      data
    }
  }

  def update(data: Team): Future[Int] = {
    db.run(team.filter(_.id === data.id).update(data))
  }

  def filterQuery(id: Long): Query[TeamTable, Team, Seq] = team.filter(_.id === id)
  def filterByNameQuery(name: String): Query[TeamTable, Team, Seq] = team.filter(_.name === name)

  def findByName(name: String): Future[Seq[Team]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def findById(id: Long) : Future[Seq[Team]] = {
    db.run( filterQuery(id).result ).map{
      dataFromDb =>
        println("Data is >>>>"+ dataFromDb)
        dataFromDb
    }
  }

  def find2(id: Long) = {
    val pp = db.run(filterQuery(id).result.head)
    pp
  }

  def find(id: Long) = {
    val pp = db.run(team.filter(_.id === id).result.head)
    pp

    /*db.run(
      DBIO.seq( people.filter(_.name === "Germany").result )
    )*/
    //db.run(people.filter(p => p.name === name))
  }

  def delete(id: Long) = {
    val pp = db.run(filterQuery(id).delete)
    pp

    /*db.run(
        DBIO.seq( people.filter(_.name === "Germany").result )
      )*/
    //db.run(people.filter(p => p.name === name))
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Team]] = db.run {
    team.result
  }

  import play.api.data._
  import play.api.data.Forms._
  import play.api.data.validation.Constraints._

  val userForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> text,
      "score" -> number,
    )(Team.apply)(Team.unapply)
  )
}

