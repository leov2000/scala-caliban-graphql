package example.akkahttp

import example.graphql.ZService.ExampleService
import example.ExampleApi

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import caliban.{ AkkaHttpAdapter, CalibanError, GraphQLInterpreter }
import example.graphql.{ ExampleEnv, ZService }
import sttp.tapir.json.circe._
import zio.{ Runtime, Unsafe }

object AccountApp extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val runtime: Runtime[ExampleEnv] =
    Unsafe.unsafe(implicit u => Runtime.unsafe.fromLayer(ZService.make(Map())))

  val interpreter: GraphQLInterpreter[ExampleEnv, CalibanError] =
    Unsafe.unsafe(implicit u => runtime.unsafe.run(ExampleApi.api.interpreter).getOrThrow())
  val adapter: AkkaHttpAdapter = AkkaHttpAdapter.default

  /** curl -X POST \
    * http://localhost:8088/api/graphql \
    * -H 'Host: localhost:8088' \
    * -H 'Content-Type: application/json' \
    * -d '{
    * "query": "query { characters { name }}"
    * }'
    */
  val route: Route =
    path("api" / "graphql") {
      adapter.makeHttpService(interpreter)
    } ~ path("ws" / "graphql") {
      adapter.makeWebSocketService(interpreter)
    } ~ path("altair") {
      getFromResource("altair.html")
    }

  val bindingFuture = Http().newServerAt("localhost", 8088).bind(route)
  println(s"Server online at http://localhost:8088/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete { _ =>
      system.terminate()
    }
}
