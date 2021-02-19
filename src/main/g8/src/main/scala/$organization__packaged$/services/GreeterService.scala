package $organization$.services

import $organization$._
import $organization$.config._
import $organization$.feature.flags._
import cats.data.EitherT
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import monix.eval.Task
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
      .map { either =>
        either.fold(
          {
            case NameCanNotBeEmpty() => ErrorResponse(
              message = "name can't be empty"
            )
          },
          res => res
        )
      }
  }

  // function
  private def tellGreeterResponse(
                                   featureFlag: ExampleFeatureFlags, // feature flag
                                   greetRequest: GreetRequest // input
                                 ): Task[Either[GenerateGreetResponse, GreetResponse]] =
    Task {
      // validation
      if (greetRequest.name.isEmpty)
        Left(
          NameCanNotBeEmpty()
        )
      // post validation business logic
      if (!featureFlag.enable) {
        Right(
          OutOfServiceResponse()
        )
      } else if (featureFlag.block.contains(greetRequest.name)) {
        Right(
          NotWelcomeResponse(
            message = s"$"$"${greetRequest.name}, you are not welcome here!"
          )
        )
      } else {
        Right(
          WelcomeResponse(
            message = "Hello, " + greetRequest.name + "!"
          )
        )
      }

    }
}
