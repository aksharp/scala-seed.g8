package $organization$

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.FixtureAnyWordSpec
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import com.tremorvideo.example._
import org.scalatest._
import com.tremorvideo.lib.api.observable.ObservableAndTraceableBase

class IntegrationTest extends FixtureAnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  type FixtureParam = Map[String, Any]

  override protected def withFixture(test: OneArgTest): Outcome = {
    test(test.configMap)
  }

  "should return WelcomeResponse" in { config: FixtureParam =>

    val env = config.getOrElse("env", fail("""include env param like so: sbt "it:testOnly * -- -Ddc=iad1 -Denv=canary" """))
    val dc = config.getOrElse("dc", fail("""include env param like so: sbt "it:testOnly * -- -Ddc=iad1 -Denv=canary" """))
    val host = if (env == "local") "localhost" else s"$"$"${env}.$name$.service.$"$"${dc}.consul"

    val client = new com.tremorvideo.example.Client(
      host = host
    )

    val name = "Alex"

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        observableAndTraceable = toObservableAndTraceable,
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

    val env = config.getOrElse("env", fail("""include env param like so: sbt "it:testOnly * -- -Ddc=iad1 -Denv=canary" """))
    val dc = config.getOrElse("dc", fail("""include env param like so: sbt "it:testOnly * -- -Ddc=iad1 -Denv=canary" """))
    val host = if (env == "local") "localhost" else s"$"$"${env}.$name$.service.$"$"${dc}.consul"

    val client = new com.tremorvideo.example.Client(
      host = host
    )

    val emptyName = ""

    val futureResult = client.greeter.greet(
      request = GreetRequest(
        observableAndTraceable = toObservableAndTraceable,
        name = emptyName
      )
    )
    val result = Await.result(futureResult, Duration.Inf)
    val expectedResult = ErrorResponse(
      message = "name can't be empty"
    )
    result should be(expectedResult)

  }

  private def toObservableAndTraceable: ObservableAndTraceableBase = {
    ObservableAndTraceableBase(
      serviceInstanceCorrelationId = "it:test",
      apiCallCorrelationId = s"api-test-$"$"${System.currentTimeMillis()}",
      apiCallTimestamp = System.currentTimeMillis()
    )
  }

}
