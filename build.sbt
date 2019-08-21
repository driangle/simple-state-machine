import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val simple_state_machine = crossProject(JVMPlatform, JSPlatform).
  crossType(CrossType.Full).
  settings(
    // Project information
    inThisBuild(List(
      organization := "org.driangle",
      organizationName := "driangle",
      organizationHomepage := Some(url("https://driangle.org")),
      scalaVersion := "2.12.6",
//      scalaVersion := "2.13.0", The compiler bridge sources org.scala-sbt:compiler-bridge_2.13:1.2.1:compile could not be retrieved
      version := "0.1.0"
    )),
    name := "simple-state-machine",
    description := "A simple, lightweight library for implementing state machines in Scala",
    homepage := Some(url("https://driangle.github.io/simple-state-machine/")),
    licenses := Seq("Apache v2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    // failure occurs when I try to use 2.13.0 due to [org.scala-sbt:compiler-bridge_2.13:1.2.1:compile could not be retrieved]
    crossScalaVersions := List("2.12.6"),

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

  ).
  jvmSettings(
    // Dependencies
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    ),

    // Reports
    jacocoReportSettings := JacocoReportSettings()
      .withThresholds(
        JacocoThresholds(
          branch = 100,
          line = 95
        )
      ),
  )

//lazy val ggr_processing_js = ggr_processing.js
//lazy val ggr_processing_jvm = ggr_processing.jvm


