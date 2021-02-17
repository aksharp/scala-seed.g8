# $organization$ project

## 1. Create ExampleFeatureFlags in consul KV store (to be automated?)
```arma.header
Consul KV: http://consul.service.iad1.consul:8500/ui/iad1/kv/

Folder Name: $name$

Feature Flag Name: ExampleFeatureFlags

Feature Flag Value:
{
  "allow": [],
  "block": [],
  "enable": true
}
```

## 2. Run example app
```
sbt 
> compile
> run iad1-prod

```
choose `$organization$.Main` when prompted

## 3. Consume gRPC endpoint through CLI

Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet
```

Expected output

```arma.header
{
  "message": "Hello, Alex!"
}
```

## 4. Observe API Call (Type Flow, Intent, Feature Flags, Input, Output or Error)
In logs (sbt window running the app) look for value starting with `api-` which is api correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/apiCorrelationId/API_CORRELATION_ID
replacing `API_CORRELATION_ID` with the actual api correlation id. 

## 5. Change Feature Flag Value and Observe
In Consul, update Feature Flag to be
```arma.header
{
  "allow": [],
  "block": ["Alex"],
  "enable": true
}
```
Go through steps #3 and #4 to observe error response

## 6. Observe Application Static Config
In logs (sbt window running the app) look for value starting with `service-$name$` which is app/service instance correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/serviceInstanceCorrelationId/SERVICE_INSTANCE_CORRELATION_ID
replacing `SERVICE_INSTANCE_CORRELATION_ID` with the actual service instance correlation id.


## 7. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181 

## 8. Run integration test against running application / service
```arma.header
sbt
> it:test
```

## 9. Run Mock Server
```
sbt 
> compile
> run

```
choose `$organization$.MockServer` when prompted`


## 10. Consume mock gRPC endpoint through CLI

Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d '{"name": "Alex"}' localhost:9090 $organization$.Greeter/Greet
```

Expected output

```arma.header
{

}
```
Mock code can be updated to return any desired response

## 11. Consume mock gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 9191 localhost:9090
```
Navigate to http://127.0.0.1:9191 

