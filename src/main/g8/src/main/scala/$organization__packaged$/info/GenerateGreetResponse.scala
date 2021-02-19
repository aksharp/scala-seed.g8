package $organization$.info

sealed trait GenerateGreetResponse {
  self =>

  // this will transform type of error message into human readable format
  val errorMessage: String = self.getClass.getSimpleName.replaceAll(
    String.format(
      "%s|%s|%s",
      "(?<=[A-Z])(?=[A-Z][a-z])",
      "(?<=[^A-Z])(?=[A-Z])",
      "(?<=[A-Za-z])(?=[^A-Za-z])"
    ),
    " "
  )

}

case class OutOfService() extends GenerateGreetResponse
case class NotWelcome(name: String) extends GenerateGreetResponse