package example.route

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import caliban.{CalibanError, GraphQLInterpreter}
import example.graphql.{ExampleApi, ExampleEnv}
import example.route.Directive.graphqlRoute
import zio.{Runtime, Unsafe}

import scala.concurrent.ExecutionContext

class HttpRoute(implicit system: ActorSystem, runtime: Runtime[ExampleEnv], ec: ExecutionContext) {

  val interpreter: GraphQLInterpreter[ExampleEnv, CalibanError] =
    Unsafe.unsafe(implicit u => runtime.unsafe.run(ExampleApi.api.interpreter).getOrThrow())

  val route: Route = graphqlRoute(interpreter)
}
