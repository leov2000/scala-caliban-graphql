package example

import example.ExampleData._
import example.ExampleService.Service
import caliban.GraphQL
import caliban.GraphQL.graphQL
import caliban.RootResolver
import caliban.schema.Annotations.{ GQLDeprecated, GQLDescription }
import caliban.schema.{ GenericSchema, Schema }
import zio.URIO

//import scala.language.postfixOps

object ExampleApi extends GenericSchema[Service] {

  case class Queries(
                      @GQLDescription("Return all characters from a given origin")
                      characters: URIO[Service, List[Character]],
                      @GQLDeprecated("Use `characters`")
                      character: CharacterArgs => URIO[Service, Option[Character]]
                    )
  case class Mutations(deleteCharacter: CharacterArgs => URIO[Service, Boolean])


  implicit val roleSchema: Schema[Any, Role]                     = Schema.gen
  implicit val characterSchema: Schema[Any, Character]           = Schema.gen
  implicit val characterArgsSchema: Schema[Any, CharacterArgs]   = Schema.gen
  implicit val charactersArgsSchema: Schema[Any, CharactersArgs] = Schema.gen

  val api: GraphQL[Service] =
    graphQL(
      RootResolver(
        Queries( ExampleService.getCharacters,
          args => ExampleService.findCharacter(args.name)
        ),
        Mutations(args => ExampleService.deleteCharacter(args.name)),
      )
    )
}