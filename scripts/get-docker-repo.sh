#! /bin/bash

BRANCH=`git rev-parse --abbrev-ref HEAD`

if [[ "$BRANCH" == "main" ]]; then
    echo "hivdb/sierra"
else
    echo "hivdb/sierra-testing"
fi
