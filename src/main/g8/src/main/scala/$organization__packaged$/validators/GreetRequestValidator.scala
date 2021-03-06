package $organization$.validators

import $organization$._
import io.github.aksharp.tc._
import com.tremorvideo.api.example._

class GreetRequestValidator[F[_]] extends Validator[F, GreetRequest, GreetResponse] {

  def requestWithEmptyName: GreetRequest => Boolean = r => r.name.isEmpty

  val validationToErrorMap: Map[GreetRequest => Boolean, GreetResponse] =
    Map(
      requestWithEmptyName -> ErrorResponse(message = "name can't be empty")
    )

}
