#!/bin/bash -x

# Setup instructions: https://github.com/fullstorydev/grpcurl

echo "Input:"
echo 'grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet'
echo "-------------------------------------------------------------------------------------"
echo "Output:"
grpcurl -plaintext -d '{"name": "Alex"}' localhost:8080 $organization$.Greeter/Greet
