lazy val simple_state_machine = (project in file(".")).
  settings(
    // Project information
    inThisBuild(List(
      organization := "org.driangle",
      organizationName := "driangle",
      organizationHomepage := Some(url("https://driangle.org")),
      scalaVersion := "2.13.0",
      version := "0.1.0-SNAPSHOT"
    )),
    name := "simple-state-machine",
    description := "A simple, lightweight library for implementing state machines in Scala",
    homepage := Some(url("https://driangle.github.io/simple-state-machine/")),
    licenses := Seq("Apache v2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

    // Dependencies
    libraryDependencies ++= Seq(
      "org.scalatest" % "scalatest_2.13" % "3.0.8" % "test"
    ),

    // Reports
    jacocoReportSettings := JacocoReportSettings()
      .withThresholds(
        JacocoThresholds(
          branch = 100,
          line = 95
        )
      ),

    // Publishing
    publishTo := sonatypePublishTo.value,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/driangle/simple-state-machine"),
        "scm:git@github.com:driangle/simple-state-machine.git"
      )
    ),
    developers := List(
      Developer(
        id = "driangle",
        name = "German Adrián Greiner",
        email = "german.greiner@gmail.com",
        url = url("https://germangreiner.com")
      )
    )

  )