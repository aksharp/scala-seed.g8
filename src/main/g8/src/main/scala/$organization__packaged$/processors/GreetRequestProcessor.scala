package $organization$.processors

import cats.Applicative
import $organization$._
import $organization$.feature.flags._
import io.github.aksharp.tc._
import com.tremorvideo.lib.api.example._

class GreetRequestProcessor[F[_]] extends Processor[F, GreetFeatureFlags, GreetRequest, GreetResponse] {

  override def process(
                        greetFeatureFlags: GreetFeatureFlags,
                        validatedRequest: GreetRequest
                      )(implicit A: Applicative[F]): F[GreetResponse] =
    A.pure {
      if (!greetFeatureFlags.enable) {
        OutOfServiceResponse()
      } else if (greetFeatureFlags.block.contains(validatedRequest.name)) {
        NotWelcomeResponse(
          message = s"$"$"${validatedRequest.name}, you are not welcome here!"
        )
      } else {
        WelcomeResponse(
          message = "Hello, " + validatedRequest.name + "!"
        )
      }
    }

}
