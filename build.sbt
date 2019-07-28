
lazy val ggr_sn= (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "ggr",
      scalaVersion := "2.13.0",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "ggr-sm",
    libraryDependencies ++= Seq()
  )