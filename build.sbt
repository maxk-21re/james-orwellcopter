name := """james-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "New Motion Repository" at "http://nexus.thenewmotion.com/content/groups/public/"


scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice
libraryDependencies += evolutions
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.0"
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.2",
  "com.vividsolutions" % "jts" % "1.13",
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
  "com.github.tminglei" %% "slick-pg" % "0.16.1",
  "com.github.tminglei" %% "slick-pg_jts" % "0.16.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.16.1",
  "com.newmotion" % "akka-rabbitmq_2.12" % "5.0.0",
  "com.typesafe.akka" %% "akka-actor" % "2.5.0",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x"
)

resolvers += "MQTT Repository" at "https://repo.eclipse.org/content/repositories/paho-releases/"
