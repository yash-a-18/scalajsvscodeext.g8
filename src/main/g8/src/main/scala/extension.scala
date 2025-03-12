import typings.vscode.mod as vscode
import typings.vscode.anon.Dispose
import scala.util.*
import scala.scalajs.js
import concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation._
import vscode.{ExtensionContext}

import typings.auroraLangium.distTypesSrcExtensionLangclientconfigMod.LanguageClientConfigSingleton

object myextension {
  val langConfig = LanguageClientConfigSingleton.getInstance()

  @JSExportTopLevel("activate")
  def activate(context: vscode.ExtensionContext): Unit = {
    langConfig.setServerModule(context.asAbsolutePath("node_modules/aurora-langium/dist/cjs/language/main.cjs"))
    println(langConfig.getServerModule())
    langConfig.initialize(context)
    langConfig.registerWebviewViewProvider()
    val outputChannel = vscode.window.createOutputChannel("My Extension")
    outputChannel.appendLine("Congratulations Team Aurora, your extension 'vscode-scalajs-aurora' is now active!")
    outputChannel.show(preserveFocus = true)

    def showHello(): js.Function1[Any, Any] =
      (arg) => {
        vscode.window.showInputBox().toFuture.onComplete {
          case Success(input) => vscode.window.showInformationMessage(s"Hello \$input!")
          case Failure(e)     => println(e.getMessage)
        }
      }

    val commands = List(
      ("myextension.aurora", showHello())
      )

    commands.foreach { case (name, fun) =>
      context.subscriptions.push(
        vscode.commands
        .registerCommand(name, fun)
        .asInstanceOf[Dispose]
        )
      }
  }

  def deactivate(): Unit = {
    langConfig.stopClient()
  }
}
