package dao

import models.Player
import play.api.data.Form

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlayerDao @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */

  //(name: String, country: String, role: Seq[String])

  class PlayerTable(tag: Tag) extends Table[Player](tag, "player") {
    implicit val stringListMapper = MappedColumnType.base[Seq[String], String](
      list => list.mkString(","),
      string => string.split(',').toSeq
    )
    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    /** The name column */
    def name = column[String]("name")
    /** The age column */
    def country = column[String]("country")
    def role: Rep[Seq[String]] = column[Seq[String]]("role")(stringListMapper)

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (id, name, country, role) <> ((Player.apply _).tupled, Player.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val player = TableQuery[PlayerTable]

  /**
   * Create a person with the given name and age.
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def insert(data: Player): Future[Player] = {
    db.run(
      DBIO.seq(
        player += data
      )
    ) recover {
      case t: Throwable =>
        println("ERROR IS " + t.getLocalizedMessage)
        throw t
    } map { d =>
      data
    }
  }

  def update(data: Player): Future[Int] = {
    db.run(player.filter(_.id === data.id).update(data))
  }

  def filterQuery(id: Long): Query[PlayerTable, Player, Seq] = player.filter(_.id === id)
  def filterByNameQuery(name: String): Query[PlayerTable, Player, Seq] = player.filter(_.name === name)

  def findByName(name: String): Future[Seq[Player]] = {
    db.run(filterByNameQuery(name).result).map {
      dataFromDb =>
        println("Data is >>>>" + dataFromDb)
        dataFromDb
    }
  }

  def findById(id: Long) : Future[Seq[Player]] = {
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
    val pp = db.run(player.filter(_.id === id).result.head)
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
  def list(): Future[Seq[Player]] = db.run {
    player.result
  }

  import play.api.data._
  import play.api.data.Forms._
  import play.api.data.validation.Constraints._

  val userForm = Form(
    mapping(
          "id" -> longNumber,
      "name" -> text,
      "country" -> text,
      "role"  -> seq(text)
    )(Player.apply)(Player.unapply)
  )
}

