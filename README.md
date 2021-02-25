This is a WORK IN PROGRESS [Giter8][g8] template for Scala seed project with dependencies on private and local artifacts.

// TODO: (near future)
1. Multi service examples (use g8 to generate multiple services and have one call another)
2. Example of high performance cacheable service (copy from bidderff)
3. Enum code generation for proto (in scalapb-grpc-client-server-mocks-codegen-plugin)
4. Observability control (example: observe flag passed so all downstream calls will get observed, even if on app level observability is turned off)
5. Traffic control (example: go through canary stack) on generated grpc client (via plugin) should check if caller passed "canary flag" and callee will also call downstream canary
6. maybe proto2slate documentation, lib-api or per service?
7. Storage examples: CSS / MySQL / Vertica / Kafka / Redis / Aerospike examples

// WORKING:
1. Automation: Proto / gRPC codegen (models, server, client, mockserver, mockclient, mocks, value generation for mocks)
2. Observability & Metrics: DataDog Metrics, Observable Static & Dynamic Config with Examples (Pure Config / Feature Flags) and Feature Flags Tests
4. Post Deployment Verification: Integration test against running application via gRPC endpoint
5. Continuous Deployment: HTTP Server for healthchecks, GitLabCI
3. (Partial) Cacheable service (examples in bidderff for now)

// DEPENDENCIES:

Libraries:
1) lib-kafka
2) lib-api
3) lib-feature-flags
4) lib-metrics
5) scala-type-classes (io.github.aksharp)

Git Repos:
1) tremorvideodsp/dspsharedlibrary
   
Applications:
1) Observable Persister

Infrastructure:
1) Consul (feature flags and service discovery)
2) Kafka Cluster
3) PostgreSQL (observable persister storage)
4) GitLab
5) Kubernetes
6) DockerHub
7) (Optionally) Redis / Aerospike for cluster shared cache

Start with this to generate a project from the template:
```
sbt new aksharp/scala-seed.g8
```

Then follow the README.md in the generated project

