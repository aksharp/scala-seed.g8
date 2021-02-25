package $organization$

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import com.tremorvideo.lib.api.example._

// todo: move to /it directory to run separately via sbt
class IntegrationTest extends FixtureAnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  type FixtureParam = Map[String, Any]

  override protected def withFixture(test: OneArgTest): Outcome = {
    test(test.configMap)
  }

  "should return WelcomeResponse" in { config: FixtureParam =>

    val env = config.getOrElse("env", fail("""include env param like so: sbt "it:testOnly * -- -Denv=iad1" """))

    val client = new com.tremorvideo.lib.api.example.Client(
      host = s"canary.$name$.service.$"$"${env}.consul"
    )

    val name = "Alex"

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = name
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = WelcomeResponse(
      message = "Hello, " + name + "!"
    )
    result should be(expectedResult)

  }

  "should get ErrorResponse when submitting with empty name" in { config: FixtureParam =>

    val env = config.getOrElse("env", fail("""include env param like so: sbt "it:testOnly * -- -Denv=iad1" """))

    val client = new com.tremorvideo.lib.api.example.Client(
      host = s"canary.$name$.service.$"$"${env}.consul"
    )

    val emptyName = ""

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = emptyName
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = ErrorResponse(
      message = "name can't be empty"
    )
    result should be(expectedResult)

  }

}
