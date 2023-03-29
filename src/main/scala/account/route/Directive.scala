package account.route

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import caliban.{ AkkaHttpAdapter, GraphQLInterpreter }
import sttp.tapir.json.circe._
import zio.Runtime

import scala.concurrent.ExecutionContext

object Directive {

  def graphqlRoute[R, E](
    interpreter: GraphQLInterpreter[R, E]
  )(implicit runtime: Runtime[R], ec: ExecutionContext, materializer: Materializer): Route = {
    val adapter: AkkaHttpAdapter = AkkaHttpAdapter.default

    path("api" / "graphql") {
      adapter.makeHttpService(interpreter)
    } ~ path("ws" / "graphql") {
      adapter.makeWebSocketService(interpreter)
    } ~ path("altair") {
      getFromResource("altair.html")
    }
  }
}
