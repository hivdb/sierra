VERSION = $(shell date -u +"%Y%m%d%H%M%S")
DOCKERREPO ?= $(shell scripts/get-docker-repo.sh)
PLATFORMS = linux/amd64,linux/arm64

sync-hivfacts:
	@rsync -avc --delete hivfacts/data/* --delete-excluded --exclude={'*/.mypy_cache','*.swp','*.swo'} hivfacts/hivfacts-java/src/main/resources

# Local builds (native platform only)
build: sync-hivfacts
	@docker build -t ${DOCKERREPO} .

build-builder: sync-hivfacts
	@docker build --target builder -t hivdb/sierra-builder .

test: build-builder
	@docker run --rm \
		-v $(shell pwd):/project \
		-w /project \
		hivdb/sierra-builder:latest \
		/sierra/gradlew --no-daemon test

test-regression: build-builder
	@docker run --rm \
		-v $(shell pwd):/project \
		-w /project \
		hivdb/sierra-builder:latest \
		/sierra/gradlew --no-daemon test -PenableRegressionTest

build-ci:
	@docker build -t hivdb/sierra-ci -f Dockerfile.CI .

build-dp:
	@echo "Build deployment version..."
	@docker build -t hivdb/sierra-dp -f Dockerfile.DP .

force-build: sync-hivfacts
	@docker build --no-cache -t ${DOCKERREPO} .

dev: build build-dp
	@docker rm -f hivdb-sierra-dev 2>/dev/null || true
	@docker run \
		--name=hivdb-sierra-dev \
		--volume ~/.aws:/root/.aws:ro \
		--env NUCAMINO_AWS_LAMBDA=nucamino:3 \
		--rm -it --publish=8111:8080 hivdb/sierra-dp dev

run-testing:
	@docker run --rm -it --publish=8111:8080 hivdb/sierra-testing:$(shell cat .latest-version) dev

inspect:
	@docker exec -it hivdb-sierra-dev /bin/bash

# Release builds
release-ci: build-ci
	@docker login
	@docker push hivdb/sierra-ci:latest

release: sync-hivfacts
	@docker login
	@docker buildx build --platform ${PLATFORMS} \
		-t ${DOCKERREPO}:${VERSION} \
		-t ${DOCKERREPO}:latest \
		--push .
	@echo ${VERSION} > .latest-version

release-dp: sync-hivfacts
	@docker login
	@docker buildx build --platform ${PLATFORMS} \
		-t hivdb/sierra-dp:$(shell cat .latest-version) \
		-t hivdb/sierra-dp:latest \
		-f Dockerfile.DP --push .

sync-to-testing:
	@docker tag hivdb/sierra:latest hivdb/sierra-testing:latest
	@docker tag hivdb/sierra:latest hivdb/sierra-testing:$(shell cat .latest-version)
	@docker push hivdb/sierra-testing:latest
	@docker push hivdb/sierra-testing:$(shell cat .latest-version)

release-testing:
	@make release DOCKERREPO=hivdb/sierra-testing

.PHONY: sync-hivfacts build build-builder test test-regression force-build dev inspect release release-ci release-dp release-testing sync-to-testing
