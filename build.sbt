name := "FirstSparkScalaProject"
version := "0.1"
scalaVersion := "2.13.8"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.3.1",
  "org.apache.spark" %% "spark-sql" % "3.3.1"
)
libraryDependencies += "io.spray" %% "spray-json" % "1.3.6"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.3"
