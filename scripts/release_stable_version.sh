#! /bin/bash

set -e

cd `dirname $0`/..
if [ -z "$1" ]; then
    echo "Usage: $0 <VERSION>" >&2
    exit 1
fi

make build release

docker tag hivdb/sierra:latest hivdb/sierra:$1
docker push hivdb/sierra:$1

make release-ci

make release-dp
docker tag hivdb/sierra-dp:latest hivdb/sierra-dp:$(cat .latest-version)
docker push hivdb/sierra-dp:$(cat .latest-version)
