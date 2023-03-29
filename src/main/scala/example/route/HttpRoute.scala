package example.route

import akka.actor.ActorSystem
import example.graphql.ExampleEnv
import zio.Runtime

import scala.concurrent.ExecutionContext

class HttpRoute(implicit system: ActorSystem, runtime: Runtime[ExampleEnv], ec: ExecutionContext) {

}

