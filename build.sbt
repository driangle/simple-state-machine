lazy val ggr_sm = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "ggr",
      scalaVersion := "2.13.0",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "ggr-sm",
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.13" % "3.0.8" % "test"
    ),
    jacocoReportSettings := JacocoReportSettings()
      .withThresholds(
        JacocoThresholds(
          branch = 100,
          line = 95
        )
      )
  )