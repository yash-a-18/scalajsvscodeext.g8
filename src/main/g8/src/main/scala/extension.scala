import typings.vscode.mod as vscode
import typings.vscode.anon.Dispose
import typings.vscode.Thenable

import scala.collection.immutable
import scala.util.*
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.UndefOr


import concurrent.ExecutionContext.Implicits.global

import scala.scalajs.js.annotation._
import scala.concurrent.Future
import vscode.{ExtensionContext}
import vscode.*

@js.native
@JSImport("vscode-languageclient/node", JSImport.Namespace)
object VscodeLanguageClient extends js.Object {
  val TransportKind: js.Dynamic = js.native
}

@js.native
trait ServerOptions extends js.Object {
  val run: js.Object = js.native
  val debug: js.Object = js.native
}

@js.native
trait LanguageClientOptions extends js.Object {
  val documentSelector: js.Array[js.Object] = js.native
}

@js.native
@JSGlobal
class LanguageClient(name: String, displayName: String, serverOptions: ServerOptions, clientOptions: LanguageClientOptions) extends js.Object {
  def start(): Unit = js.native
  def stop(): js.Promise[Unit] = js.native
}

object $extensionprefix$ {
  private var client: Option[LanguageClient] = None

  @JSExportTopLevel("activate")
  def activate(context: vscode.ExtensionContext): Unit = {
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
      ("$extensionprefix$.aurora", showHello())
      )
      
    commands.foreach { case (name, fun) =>
      context.subscriptions.push(
        vscode.commands
        .registerCommand(name, fun)
        .asInstanceOf[Dispose]
        )
      }
      
    client = Some(startLanguageClient(context))
  }

  def deactivate(): Future[Unit] = {
    client match {
      case Some(c) => c.stop().toFuture
      case None    => Future.successful(())
    }
  }

  private def startLanguageClient(context: ExtensionContext): LanguageClient = {
    val serverModule = context.asAbsolutePath("out/language/main.cjs")
    val debugOptions = js.Dynamic.literal(
      execArgv = js.Array("--nolazy", s"--inspect\${if (js.Dynamic.global.process.env.DEBUG_BREAK.asInstanceOf[Boolean]) "-brk" else ""}=\${js.Dynamic.global.process.env.DEBUG_SOCKET.getOrElse("6009")}")
    )

    val serverOptions = js.Dynamic.literal(
      run = js.Dynamic.literal(module = serverModule, transport = VscodeLanguageClient.TransportKind.ipc),
      debug = js.Dynamic.literal(module = serverModule, transport = VscodeLanguageClient.TransportKind.ipc, options = debugOptions)
    ).asInstanceOf[ServerOptions]

    val clientOptions = js.Dynamic.literal(
      documentSelector = js.Array(js.Dynamic.literal(scheme = "*", language = "aurora"))
    ).asInstanceOf[LanguageClientOptions]

    val client = new LanguageClient("aurora", "Aurora", serverOptions, clientOptions)
    client.start()
    client
  }
}
