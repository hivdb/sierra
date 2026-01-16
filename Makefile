VERSION = $(shell date -u +"%Y%m%d%H%M%S")
DOCKERREPO ?= $(shell scripts/get-docker-repo.sh)

sync-hivfacts:
	@rsync -avc --delete hivfacts/data/* --delete-excluded --exclude={'*/.mypy_cache','*.swp','*.swo'} hivfacts/hivfacts-java/src/main/resources

build: sync-hivfacts
	@docker build -t ${DOCKERREPO} .

build-ci:
	@docker build -t hivdb/sierra-ci -f Dockerfile.CI .

build-dp:
	@echo "Build deployment version..."
	@docker build --pull -t hivdb/sierra-dp -f Dockerfile.DP .

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

release-ci: build-ci
	@docker login
	@docker push hivdb/sierra-ci:latest

release-dp: build-dp
	@docker login
	@docker tag hivdb/sierra-dp:latest hivdb/sierra-dp:$(shell cat .latest-version)
	@docker push hivdb/sierra-dp:$(shell cat .latest-version)
	@docker push hivdb/sierra-dp:latest

release:
	@docker login
	@docker tag ${DOCKERREPO}:latest ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:latest
	@echo ${VERSION} > .latest-version
	@sleep 2

sync-to-testing:
	@docker tag hivdb/sierra:latest hivdb/sierra-testing:latest
	@docker tag hivdb/sierra:latest hivdb/sierra-testing:$(shell cat .latest-version)
	@docker push hivdb/sierra-testing:latest
	@docker push hivdb/sierra-testing:$(shell cat .latest-version)

release-testing:
	@make release DOCKERREPO=hivdb/sierra-testing

.PHONY: sync-hivfacts build force-build dev inspect release release-testing
