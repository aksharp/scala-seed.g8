package $organization$.processors

import cats.Applicative
import $organization$.util.LogInitialization

trait Processor[F[_], FF, I, O] extends LogInitialization {

  def process(
               featureFlags: FF,
               validatedRequest: I
             )
             (implicit A: Applicative[F]): F[O]
}
