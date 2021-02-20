package $organization$.validators

import cats.Applicative

import scala.annotation.tailrec

trait Validator[F[_], I, E] {

  protected[validators] val validationToErrorMap: Map[I => Boolean, E]

  def validate(
                item: I
              )
              (
                implicit A: Applicative[F]
              ): F[Either[E, I]] = {

    @tailrec
    def rec(
             map: Map[I => Boolean, E] = validationToErrorMap,
             err: Option[E] = None
           ): Option[E] = {
      map.headOption match {
        case Some((f, e)) => {
          if (f(item)) Option(e)
          else rec(map.tail)
        }
        case None => err
      }
    }

    val maybeError: Option[E] = rec()

    A.pure(
      maybeError.map(Left(_)).getOrElse(Right(item))
    )
  }

}


