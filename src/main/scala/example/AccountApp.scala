package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import example.graphql.{ ExampleEnv, ZService }
import example.route.HttpRoute
import zio.{ Runtime, Unsafe }

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object AccountApp extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val runtime: Runtime[ExampleEnv] =
    Unsafe.unsafe(implicit u => Runtime.unsafe.fromLayer(ZService.make(Map())))

  val bindingFuture = Http().newServerAt("localhost", 8088).bind(new HttpRoute().route)
  println(s"Server online at http://localhost:8088/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete { _ =>
      system.terminate()
    }
}
