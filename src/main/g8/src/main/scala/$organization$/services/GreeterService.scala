package $organization$.services

import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import cats.implicits._
import monix.eval.Task
import monix.execution.Scheduler
import $organization$.info._

// service contract (interface)
trait GreeterService[F[_]] extends LoggableService {

  // service api
  def process(
               greetRequest: GreetRequest
             )
             (
               implicit observableAndTraceable: ObservableAndTraceable // to make api observable
             ): F[GreetResponse]

}

// service implementation
class GreeterServiceImpl(
                          implicit observableAndTraceableService: ObservableAndTraceableService[Task],
                          appConfig: AppConfig
                        ) extends GreeterService[Task] {

  // service api implementation
  override def process(
                        greetRequest: GreetRequest
                      )
                      (
                        implicit observableAndTraceable: ObservableAndTraceable // to make api observable
                      ): Task[GreetResponse] = {

    // sequence of dynamically configured functions
    (for {
      greetResponse <- EitherT(
        ExampleFeatureFlags.runAndObserve( // observable feature flag
          action = tellGreeterResponse, // observable function
          input = greetRequest // observable function input
        )
      )
    } yield {
      greetResponse
    }).value

  }

  // function
  private def tellGreeterResponse(
                                   featureFlag: ExampleFeatureFlags, // feature flag
                                   greetRequest: GreetRequest // input
                                 ): Task[Either[GenerateGreetResponse, GreetResponse]] =
    Task {

      if (!featureFlag.enable) {
        Left(
          OutOfService()
        )
      } else if (featureFlag.block.contains(greetRequest.name)) {
        Left(
          NotWelcome(
            name = greetRequest.name
          )
        )
      } else {
        Right(
          GreetResponse(
            message = "Hello, " + greetRequest.name + "!"
          )
        )
      }

    }
}
