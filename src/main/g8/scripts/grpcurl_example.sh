#!/bin/bash -x

# Setup instructions: https://github.com/fullstorydev/grpcurl

echo 'grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet'
grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet
