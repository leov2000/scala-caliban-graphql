package example.akkahttp

import example.ExampleData.sampleCharacters
import example.ExampleService.{Service, ServiceEnv}
import example.{ExampleApi, ExampleService}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import caliban.{AkkaHttpAdapter, CalibanError, GraphQLInterpreter}
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerOptions
import zio.{Runtime, Unsafe}

object ExampleApp extends App {

  implicit val system: ActorSystem                                      = ActorSystem()
  implicit val executionContext: ExecutionContextExecutor               = system.dispatcher
  implicit val runtime: Runtime[ServiceEnv] = Unsafe.unsafe{ implicit unsafe =>
    Runtime.unsafe.fromLayer(ExampleService.make(Map()))
  }

  val interpreter: GraphQLInterpreter[ServiceEnv, CalibanError] = Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(ExampleApi.api.interpreter).getOrThrowFiberFailure()
  }


  /**
   * curl -X POST \
   * http://localhost:8088/api/graphql \
   * -H 'Host: localhost:8088' \
   * -H 'Content-Type: application/json' \
   * -d '{
   * "query": "query { characters { name }}"
   * }'
   */
  val route: Route =
    path("api" / "graphql") {
      AkkaHttpAdapter(AkkaHttpServerOptions.default).makeHttpService(interpreter)
    } ~ path("altair") {
      getFromResource("altair.html")
    }

  val bindingFuture = Http().newServerAt("localhost", 8088).bind(route)
  println(s"Server online at http://localhost:8088/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

