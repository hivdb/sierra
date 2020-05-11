#! /bin/bash

set -e

cd `dirname $0`/..
if [ -z "$1" -o -z "$2" ]; then
    echo "Usage: $0 <VERSION> YYYY-MM-DD" >&2
    exit 1
fi

find . -name "build.gradle" -a -not -path "./hivfacts/*" -a -not -path "./asi_interpreter/*" | xargs sed -i "" "s/^version = '.*'$/version = '$1'/g"
mkdir -p WebApplications/src/main/resources/
echo "version = $1
versionDate = $2" > WebApplications/src/main/resources/version.properties
