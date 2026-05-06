PLATFORMS = linux/amd64,linux/arm64

build:
	@docker buildx build --platform ${PLATFORMS} \
		-t hivdb/tomcat-with-postalign:latest

force-build:
	@docker build --no-cache -t hivdb/tomcat-with-postalign:latest .

inspect:
	@docker rm -f inspect-tomcat-postalign 2>/dev/null || true
	@docker run --name=inspect-tomcat-postalign --rm -it --entrypoint /bin/bash hivdb/tomcat-with-postalign:latest || true

release:
	@docker login
	@docker buildx build --platform ${PLATFORMS} \
		-t hivdb/tomcat-with-postalign:latest \
		--push .

.PHONY: build force-build inspect release
