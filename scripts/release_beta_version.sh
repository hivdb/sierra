#! /bin/bash

set -e

cd `dirname $0`/..
if [ -z "$1" ]; then
    echo "Usage: $0 <VERSION>" >&2
    exit 1
fi

cd docker/sierra; make release
docker tag hivdb/sierra-testing:latest hivdb/sierra:$1
docker push hivdb/sierra:$1
