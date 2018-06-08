sierra_container_version = $(shell cat docker/sierra/.latest-version)

build:
	@cd docker/sierra; make build

dev: build
	@docker rm -f hivdb-sierra-dev 2>/dev/null || true
	@docker run \
		--name=hivdb-sierra-dev \
		--rm -it --publish=8111:8080 hivdb/sierra dev

inspect:
	@docker exec -it hivdb-sierra-dev /bin/bash

release:
	@cd docker/sierra; make release
