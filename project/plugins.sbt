// enables code coverage reports
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.0.3")

// enables publishing to sonatype repository.
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

// Enables Cross Compiling
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.1")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"                   % "1.0.0-M8")