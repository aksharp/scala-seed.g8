# $organization$ project

## 0. Create ExampleFeatureFlags in consul KV store (to be automated?)
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

## 1. Run example app
```
sbt 
> compile
> run iad1-prod

```
choose `$organization$.Main` when prompted

## 2. Consume gRPC endpoint through CLI

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

## 3. Observe API Call (Type Flow, Intent, Feature Flags, Input, Output or Error)
In logs (sbt window running the app) look for value starting with `api-` which is api correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/apiCorrelationId/API_CORRELATION_ID
replacing `API_CORRELATION_ID` with the actual api correlation id. 

## 4. Change Feature Flag Value and Observe
In Consul, update Feature Flag to be
```arma.header
{
  "allow": [],
  "block": ["Alex"],
  "enable": true
}
```
Go through steps #2 and #3 to observe error response

## 5. Observe Application Static Config
In logs (sbt window running the app) look for value starting with `service-$name$` which is app/service instance correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/serviceInstanceCorrelationId/SERVICE_INSTANCE_CORRELATION_ID
replacing `SERVICE_INSTANCE_CORRELATION_ID` with the actual service instance correlation id.


## 6. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181 


## 7. Run Mock Server
```
sbt 
> compile
> run

```
choose `$organization$.MockServer` when prompted`


## 8. Consume mock gRPC endpoint through CLI

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

## 9. Consume mock gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 9191 localhost:9090
```
Navigate to http://127.0.0.1:9191 

