# $organization$ project

##1. Run example app
```
sbt 
> compile
> run

```
choose `example.ExampleMain` when prompted


##2. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181 

##3. Consume gRPC endpoint through CLI

Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d {name: Alex} localhost:8080 com.tremorvideo.Greeter/Greet
```

Expected output

```arma.header
{
  "message": "Hello, Alex!"
}
```

##4. Run Mock Server
```
sbt 
> compile
> run

```
choose `$organization$.MockServer` when prompted`

**Note:** _Only one, example main or mock server can run at a time, as both use port 8080_