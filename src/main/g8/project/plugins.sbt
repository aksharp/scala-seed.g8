addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.4.1")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.27")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.10.0-M1"

// TODO: aksharp scalapb plugin when published
libraryDependencies += "aksharp" %% "scalapb-grpc-client-server-mocks-codegen-plugin" % "0.1.0-SNAPSHOT"
