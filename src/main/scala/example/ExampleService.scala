package example

import example.ExampleData._
import zio.stream.ZStream
import zio.{ULayer, URIO, ZIO, ZLayer}

object ExampleService {
  type ServiceEnv = Service

  trait Service {
    def getCharacters: URIO[ServiceEnv, List[Character]]

    def findCharacter(name: String): URIO[ServiceEnv, Option[Character]]

    def deleteCharacter(name: String): URIO[ServiceEnv, Boolean]

  }

  def getCharacters: URIO[ServiceEnv, List[Character]] =
    ZIO.serviceWith[Service](_.getCharacters).flatten
  def findCharacter(name: String): URIO[ServiceEnv, Option[Character]] =
    ZIO.serviceWith[ServiceEnv](_.findCharacter(name)).flatten

  def deleteCharacter(name: String): URIO[ServiceEnv, Boolean] =
    ZIO.serviceWith[ServiceEnv](_.deleteCharacter(name)).flatten

  def make(characters: Map[String, Character]):ULayer[ServiceEnv] =
      ZLayer.succeed {
        new Service {

          def getCharacters: URIO[ServiceEnv, List[Character]] =
            ZIO.succeed(characters.values.toList)

          def findCharacter(name: String): URIO[ServiceEnv, Option[Character]] = ZIO.succeed(characters.get(name))

          def deleteCharacter(name: String): URIO[ServiceEnv, Boolean] = ZIO.succeed(false)


        }
      }
}