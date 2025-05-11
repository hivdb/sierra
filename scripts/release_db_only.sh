#! /bin/bash

set -e

make build
make release
make release-dp
docker tag hivdb/sierra-dp:latest hivdb/sierra-dp:$(cat .latest-version)
docker push hivdb/sierra-dp:$(cat .latest-version)
