This is a WORK IN PROGRESS [Giter8][g8] template for Scala seed project with dependencies on private and local artifacts.

// TODO: (near future)
1. Descriptive error message in case of failure. Currently only shows Higher Kinded Type (lib feature flags)
2. Move integration tests to it (copy from hamsa)
3. Property based testing with Scalacheck (copy from bidderff) 
4. Deployment (gitlab + central deploy, copy from hamsa)
5. Multi service examples (use g8 to generate multiple services and have one call another)
6. Add http4s server
7. OneOf code generation
8. Example of high performance cacheable service (copy from bidderff)


// WORKING:
1. Proto / gRPC codegen (models, server, client, mockserver, mockclient, mocks, value generation for mocks)
2. Observable Static & Dynamic Config with Examples (Pure Config / Feature Flags) and Feature Flags Tests
3. Cacheable service (examples in bidderff for now)
4. Integration test via gRPC endpoint

Start with this to generate a project from the template:
```
sbt new aksharp/scala-seed.g8
```

Then follow the README.md in the generated project

