package $organization$.services

import cats.data.EitherT
import $organization$._
import $organization$.config.AppConfig
import $organization$.feature.flags.GreetFeatureFlags
import com.tremorvideo.lib.api.ObservableAndTraceable
import com.tremorvideo.lib.api.fp.util.ObservableAndTraceableService
import $organization$.processors.Processor
import $organization$.validators.Validator
import monix.eval.Task
import monix.execution.Scheduler.global

import scala.concurrent.Future

class GreeterService(
                      greetRequestValidator: Validator[Task, GreetRequest, GreetResponse],
                      greetRequestProcessor: Processor[Task, GreetFeatureFlags, GreetRequest, GreetResponse]
                    )
                    (
                      implicit observableAndTraceableService: ObservableAndTraceableService[Task],
                      appConfig: AppConfig
                    ) extends GreeterGrpc.Greeter {

  override def greet(greetRequest: GreetRequest): Future[GreetResponse] = {

    implicit val ot: ObservableAndTraceable = greetRequest.observableAndTraceable

    (for {
      finalResponse <- GreetFeatureFlags.runAndObserve(
        action = validateAndProcess,
        input = greetRequest
      )
    } yield {
      finalResponse
    }).runToFuture(global)
  }

  private def validateAndProcess(
                                  greetFeatureFlags: GreetFeatureFlags,
                                  greetRequest: GreetRequest
                                ): Task[GreetResponse] = {
    (for {
      validatedRequest <- EitherT[Task, GreetResponse, GreetRequest](
        greetRequestValidator.validate(
          item = greetRequest
        )
      )
      response <- EitherT.liftF[Task, GreetResponse, GreetResponse](
        greetRequestProcessor.process(
          featureFlags = greetFeatureFlags,
          validatedRequest = validatedRequest
        )
      )
    } yield {
      response
    }).value.map(_.merge)
  }

}

