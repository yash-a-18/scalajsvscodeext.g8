import scala.sys.process._

lazy val installDependencies = Def.task[Unit] {
  val base = (ThisProject / baseDirectory).value
  val log = (ThisProject / streams).value.log
  if (!(base / "node_module").exists) {
    val pb =
      new java.lang.ProcessBuilder("npm.cmd", "install")
        .directory(base)
        .redirectErrorStream(true)

    pb ! log
  }
}

lazy val open = taskKey[Unit]("Open VSCode extension based on platform")

open := {
  val base = baseDirectory.value
  val log = streams.value.log
  val path = base.getCanonicalPath


  val osName = System.getProperty("os.name").toLowerCase
  val platform =
    if (osName.contains("win")) "win32"
    else if (osName.contains("mac")) "darwin"
    else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) "linux"
    else "unknown"

  val command = platform match {
    case "win32" => s"code.cmd --extensionDevelopmentPath=\$path"
    case _       => s"code --extensionDevelopmentPath=\$path"
  }

  log.info(s"Detected platform: \$platform")
  log.info(s"Running: \$command")

  val result = command.!(log)
  if (result != 0) sys.error("Failed to open VSCode")
}


lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := DependencyVersions.scala,
    moduleName := "my-scalajs-vscode-extension",
    Compile / fastOptJS / artifactPath := baseDirectory.value / "out" / "extension.js",
    Compile / fullOptJS / artifactPath := baseDirectory.value / "out" / "extension.js",
    open := open.dependsOn(Compile / fastOptJS).value,
    // CommonJS
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    libraryDependencies ++= Dependencies.scalatest.value,

    // Compile / npmDependencies ++= Seq("@types/vscode" -> "1.84.1"),
    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value,

    testFrameworks += new TestFramework("utest.runner.Framework")
    // publishMarketplace := publishMarketplaceTask.dependsOn(fullOptJS in Compile).value
  )
  .enablePlugins(
    ScalaJSPlugin,
    ScalablyTypedConverterExternalNpmPlugin
    // ScalablyTypedConverterPlugin
  )
