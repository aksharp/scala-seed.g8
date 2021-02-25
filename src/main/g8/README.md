# $organization$ project

## 1. Create GreetFeatureFlags in consul KV store (to be automated)
```arma.header
Consul KV: http://consul.service.iad1.consul:8500/ui/iad1/kv/

Folder Name: $name$

Feature Flag Name: GreetFeatureFlags

Feature Flag Value:
{
  "allow": [],
  "block": [],
  "enable": true
}
```

## 2. Initialize. Run init.sh to init git and compile. Create `$name$` gitlab repo, add remote repository and push to gitlab
```arma.header
./init.sh

// Example:
git remote add origin git@git.tremorvideodsp.com:vh/$name$.git
git push -u origin master
```

## 3. Deploy from GitLab to canary and verify
Deploy to `iad1-canary` from GitLab Pipeline https://git.tremorvideodsp.com/vh/$name$/-/pipelines
Test by running `iad1-canary test` gitlab job from same pipeline 

## 4. Observe on observable-persister
Go to kube logs (because we don't have central logging yet) and pull out correlation ids, then plug them into observable persister to observe
Look for value starting with `api-` which is api correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/apiCorrelationId/API_CORRELATION_ID
replacing `API_CORRELATION_ID` with the actual api correlation id.

## 5. Consume gRPC endpoint through CLI

Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet
```

Expected output

```arma.header
{
  "welcomeResponse": {
    "message": "Hello, Alex!"
  }
}
```

## 6. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181


## 7. Change Feature Flag Value and Observe
In Consul, update Feature Flag to be
```arma.header
{
  "allow": [],
  "block": ["Alex"],
  "enable": true
}
```
Run request from CLI or UI and then Observe using api correlation id on observable-persister

## 8. Observe Application Static Config
In logs (sbt window running the app) look for value starting with `service-$name$` which is app/service instance correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/serviceInstanceCorrelationId/SERVICE_INSTANCE_CORRELATION_ID
replacing `SERVICE_INSTANCE_CORRELATION_ID` with the actual service instance correlation id.


## 9. Compile, Test and Run $name$ app locally
```
sbt 
> compile
> test
> run local
```

## 10. Integration test against running local instance
In another terminal window 
```
sbt "it:testOnly * -- -Denv=local"
```


## 11. In source code, check out Unit Tests and Property based testing with an example in tests



