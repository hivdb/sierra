VERSION = $(shell date -u +"%Y%m%d%H%M%S")
DOCKERREPO ?= $(shell scripts/get-docker-repo.sh)

build:
	@docker build -t ${DOCKERREPO} .

force-build:
	@docker build --no-cache -t ${DOCKERREPO} .

dev: build
	@docker rm -f hivdb-sierra-dev 2>/dev/null || true
	@docker run \
		--name=hivdb-sierra-dev \
		--volume ~/.aws:/root/.aws:ro \
		--env NUCAMINO_AWS_LAMBDA=nucamino:3 \
		--rm -it --publish=8111:8080 ${DOCKERREPO} dev

inspect:
	@docker exec -it hivdb-sierra-dev /bin/bash

release:
	@docker login
	@docker tag ${DOCKERREPO}:latest ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:latest
	@echo ${VERSION} > .latest-version
	@sleep 2

release-testing:
	@cd docker/sierra; make release DOCKERREPO=hivdb/sierra-testing

.PHONY: build force-build dev inspect release release-testing
