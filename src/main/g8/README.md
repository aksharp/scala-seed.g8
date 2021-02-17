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

## 3. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181 


## 4. Run Mock Server
```
sbt 
> compile
> run

```
choose `$organization$.MockServer` when prompted`


## 5. Consume mock gRPC endpoint through CLI

Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d '{"name": "Alex"}' localhost:9090 $organization$.Greeter/Greet
```

Expected output

```arma.header
{

}
```


## 6. Consume mock gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 9191 localhost:9090
```
Navigate to http://127.0.0.1:9191 

