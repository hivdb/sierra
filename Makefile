sierra_container_version = $(shell cat docker/sierra/.latest-version)
DOCKERREPO ?= $(shell docker/sierra/get-docker-repo.sh)

build:
	@cd docker/sierra; make build DOCKERREPO=$(DOCKERREPO)

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
	@cd docker/sierra; make release DOCKERREPO=$(DOCKERREPO)

release-testing:
	@cd docker/sierra; make release DOCKERREPO=hivdb/sierra-testing
