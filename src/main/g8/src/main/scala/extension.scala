import typings.vscode.mod as vscode
import typings.vscode.anon.Dispose
import typings.vscode.Thenable

import scala.collection.immutable
import scala.util.*
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.UndefOr


import concurrent.ExecutionContext.Implicits.global

object $extensionprefix$ {
  @JSExportTopLevel("activate")
  def activate(context: vscode.ExtensionContext): Unit = {

    val outputChannel = vscode.window.createOutputChannel("My Extension")
    outputChannel.appendLine("Congratulations Arnie, your extension 'vscode-scalajs-hello' is now active!")
    outputChannel.show(preserveFocus = true)

    def showHello(): js.Function1[Any, Any] =
      (arg) => {
        vscode.window.showInputBox().toFuture.onComplete {
          case Success(input) => vscode.window.showInformationMessage(s"Hello Arnold \$input!")
          case Failure(e)     => println(e.getMessage)
        }
      }

    val commands = List(
      ("$extensionprefix$.helloWorld", showHello())
    )

    commands.foreach { case (name, fun) =>
      context.subscriptions.push(
        vscode.commands
          .registerCommand(name, fun)
          .asInstanceOf[Dispose]
      )
    }

  }
}
