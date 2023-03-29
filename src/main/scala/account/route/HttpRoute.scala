package account.route

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import caliban.{CalibanError, GraphQLInterpreter}
import account.graphql.{AccountApi, AccountEnv}
import account.route.Directive.graphqlRoute
import zio.{Runtime, Unsafe}

import scala.concurrent.ExecutionContext

class HttpRoute(implicit system: ActorSystem, runtime: Runtime[AccountEnv], ec: ExecutionContext) {

  val interpreter: GraphQLInterpreter[AccountEnv, CalibanError] =
    Unsafe.unsafe(implicit u => runtime.unsafe.run(AccountApi.api.interpreter).getOrThrow())

  val route: Route = graphqlRoute(interpreter)
}
