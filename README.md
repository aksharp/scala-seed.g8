This is a WORK IN PROGRESS [Giter8][g8] template for Scala seed project with dependencies on private and local artifacts.

// TODO: (near future)
2. Move integration tests to it (copy from hamsa)
3. Property based testing with Scalacheck (copy from bidderff) 
4. Deployment (gitlab + central deploy, copy from hamsa)
5. Multi service examples (use g8 to generate multiple services and have one call another)
6. Add http4s server
7. OneOf code generation
8. Example of high performance cacheable service (copy from bidderff)
9. On client generation, host need to be generated: $name$.service.$data-center$.consul 
10. maybe proto2slate documentation, lib-api or per service?
11. Traffic control (example: go through canary stack) on generated grpc client (via plugin) should check if caller passed "canary flag" and callee will also call downstream canary
12. Observability control (example: observe flag passed so all downstream calls will get observed, even if on app level observability is turned off)

// WORKING:
1. Proto / gRPC codegen (models, server, client, mockserver, mockclient, mocks, value generation for mocks)
2. Observable Static & Dynamic Config with Examples (Pure Config / Feature Flags) and Feature Flags Tests
3. Cacheable service (examples in bidderff for now)
4. Integration test via gRPC endpoint

// DEPENDENCIES:

Libraries:
1) lib-kafka
2) lib-api
3) lib-feature-flags

Plugins:
1) ScalaPB
4) scalapb-grpc-client-server-mocks-codegen-plugin
   
Applications:
1) Observable Persister
2) Hamsa (optional)

Infrastructure:
1) Consul (feature flags and service discovery)
2) Kafka Cluster
3) PostgreSQL (observable persister storage)

Start with this to generate a project from the template:
```
sbt new aksharp/scala-seed.g8
```

Then follow the README.md in the generated project

