ThisBuild / scalaVersion := "2.13.8"

ThisBuild / version := "1.0-SNAPSHOT"
//libraryDependencies += "org.mockito" % "mockito-core" % "3.11.2" % Test


lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """eventmanagement""",
    libraryDependencies ++= Seq(
      evolutions,
      guice,
      "org.mockito" % "mockito-core" % "3.11.2" % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
      specs2 % Test,
      "org.scalatestplus" %% "mockito-4-5" % "3.2.12.0" % "it,test",
      "mysql" % "mysql-connector-java" % "8.0.27",
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0" % Test,
      "com.typesafe.akka" %% "akka-actor" % "2.5.24"
    )

  )