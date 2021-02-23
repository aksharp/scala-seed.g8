resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.0")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.10.10"

libraryDependencies += "io.github.aksharp" %% "scalapb-codegen-plugin" % "0.2.1-SNAPSHOT"
