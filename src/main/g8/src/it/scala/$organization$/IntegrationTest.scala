package $organization$

import $organization$._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

// todo: move to /it directory to run separately via sbt
class IntegrationTest extends AnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  // TODO: start server before testing. Unignore test afterwards

  "greeter.greet test. Example test against running server returning expected GreetResponse result" in {

    val name = "Alex"

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        name = name
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = GreetResponse(
      message = "Hello, " + name + "!"
    )
    result should be(expectedResult)

  }

}
