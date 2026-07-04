#! /bin/bash

set -e

cd `dirname $0`/..
if [ -z "$1" ]; then
    echo "Usage: $0 <VERSION>" >&2
    exit 1
fi

make release

# Tag the dynamic version as the stable version
docker buildx imagetools create \
    -t hivdb/sierra:$1 \
    hivdb/sierra:$(cat .latest-version)

make release-ci
make release-dp

echo "Released sierra $1 (amd64 + arm64)"
