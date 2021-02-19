This is a WORK IN PROGRESS [Giter8][g8] template for Scala seed project with dependencies on private and local artifacts.

// TODO: (near future)
1. Deployment (gitlab + central deploy, copy from hamsa) comment out MockServerMain codegen, update README
2. Property based testing with Scalacheck (copy from bidderff)
3. On client generation, host need to be generated: $name$.service.$data-center$.consul
4. Multi service examples (use g8 to generate multiple services and have one call another)
5. Example of high performance cacheable service (copy from bidderff)
6. Enum code generation for proto (in scalapb-grpc-client-server-mocks-codegen-plugin)
7. Observability control (example: observe flag passed so all downstream calls will get observed, even if on app level observability is turned off)
8. Traffic control (example: go through canary stack) on generated grpc client (via plugin) should check if caller passed "canary flag" and callee will also call downstream canary
9. maybe proto2slate documentation, lib-api or per service?

// WORKING:
1. Automation: Proto / gRPC codegen (models, server, client, mockserver, mockclient, mocks, value generation for mocks)
2. Observability & Metrics: DataDog Metrics, Observable Static & Dynamic Config with Examples (Pure Config / Feature Flags) and Feature Flags Tests
4. Post Deployment Verification: Integration test against running application via gRPC endpoint
5. (Partial) Continuous Deployment: HTTP Server for healthchecks
3. (Partial) Cacheable service (examples in bidderff for now)

// DEPENDENCIES:

Libraries:
1) lib-kafka
2) lib-api
3) lib-feature-flags
4) lib-metrics

Plugins:
1) ScalaPB
4) scalapb-grpc-client-server-mocks-codegen-plugin

Git Repos:
1) tremorvideodsp/dspsharedlibrary
   
Applications:
1) Observable Persister
2) Hamsa (optional)

Infrastructure:
1) Consul (feature flags and service discovery)
2) Kafka Cluster
3) PostgreSQL (observable persister storage)
4) GitLab

Start with this to generate a project from the template:
```
sbt new aksharp/scala-seed.g8
```

Then follow the README.md in the generated project

