# $organization$ project

## Detailed Steps
## 1. DO THIS FIRST
You need `dev` tool that automates lots of manual scripts. Get it from here:
`git clone git@github.com:aksharp/dev.git`
Then add alias to it in your shell (zshrc or bashrc) and restart terminal
`alias dev="~/path-to-dev-project/dev"`

## 2. Create GreetFeatureFlags in consul KV store (to be automated)
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

## 3. Create git repo on GitLab and copy the repo's git url (example: `git@git.tremorvideodsp.com:vh/$name$.git`)

## 4 Locally in terminal cd to $name$ project directory and run
`dev init GIT_REPO_URL` where GIT_REPO_URL is the git url to your repo from step #2

## 5. On GitLab deploy to canary
Deploy to `iad1-canary` from GitLab Pipeline https://git.tremorvideodsp.com/vh/$name$/-/pipelines

## 6. Verify canary deployment by running a test from GitLab pipeline
Test by running `iad1-canary test` gitlab job from same pipeline 

## 7. Observe on observable-persister
Go to kube logs (because we don't have central logging yet) and pull out correlation ids, then plug them into observable persister to observe
Look for value starting with `api-` which is api correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/apiCorrelationId/API_CORRELATION_ID
replacing `API_CORRELATION_ID` with the actual api correlation id.

## 8. Consume gRPC endpoint through CLI
Pre-requisite: `grpcurl` (setup instructions: https://github.com/fullstorydev/grpcurl)

```
grpcurl -plaintext -d '{"name": "Alex"}' canary.$name$.service.iad1.consul:8080 $organization$.Greeter/Greet
```

Expected output
```arma.header
{
  "welcomeResponse": {
    "message": "Hello, Alex!"
  }
}
```

## 9. Consume gRPC endpoint through UI

Pre-requisite: `grpcui` (setup instructions: https://github.com/fullstorydev/grpcui)

In a separate terminal window start `grpcui`
```
grpcui -plaintext -port 8181 localhost:8080
```
Navigate to http://127.0.0.1:8181


## 10. Change Feature Flag Value and Observe
In Consul, update Feature Flag to be
```arma.header
{
  "allow": [],
  "block": ["Alex"],
  "enable": true
}
```
Run request from CLI or UI and then Observe using api correlation id on observable-persister

## 11. Observe Application Static Config
In logs (sbt window running the app) look for value starting with `service-$name$` which is app/service instance correlation id. Copy it and navigate to:
http://observable-persister.service.iad1.consul:8888/serviceInstanceCorrelationId/SERVICE_INSTANCE_CORRELATION_ID
replacing `SERVICE_INSTANCE_CORRELATION_ID` with the actual service instance correlation id.

Also found in app logs on startup

## 12. Compile, Test and Run $name$ app locally
```
sbt 
> compile
> test
> run local
```

## 13. Integration test against running local instance
In another terminal window from $name$ directory
```
dev test local
```

## 14. You can also test against global canaries or prod deployments
In another terminal window from $name$ directory
```
dev test iad1 canary
dev test eu1 canary
dev test ap1 canary
```
or prod
```
dev test iad1 prod
dev test eu1 prod
dev test ap1 prod
```

## 15. In source code, check out Unit Tests and Property based testing with an example in tests


-----------
-----------
## If you are already familiar with detailed steps above, here is the summary
```arma.header
dev new
cd $name$
```
create new git repo on gitlab
```
dev init YOUR_GIT_REPO
```
delete example Greeter code

Add API proto definitions in lib-api and publish it

Update build.sbt with new lib-api version

Write code behind the api