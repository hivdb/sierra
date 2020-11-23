VERSION = $(shell date -u +"%Y%m%d%H%M%S")
DOCKERREPO ?= $(shell ./get-docker-repo.sh)

.build: $(shell find `greadlink -f ../..` -type f -not -path "./.git*" -a -not -path "*.class" -a -not -path "*/.DS_Store" -a -not -path "*/.gradle/*" -a -not -path "*/build/*" -a -not -path "*/target/*" -a -not -path "*/.settings/*" -a -not -path "*.log" -a -not -path "*/__output/*" -a -not -path "docker/*" | sed 's#\([| ]\)#\\\1#g')
	@mkdir -p target
	@cd ../..; docker run -it --rm -v $(shell realpath ${PWD}/../..):/sierra -v ${PWD}/gradle:/root/.gradle -w /sierra --entrypoint /sierra/gradlew hivdb/tomcat-with-nucamino:latest assemble
	@mv ../../WebApplications/build/libs/WebApplications-*.war target/WebApplications.war
	@touch .build

build: .build
	@docker build -t ${DOCKERREPO} .

force-build: .build
	@docker build --no-cache -t ${DOCKERREPO} .

release: build
	@docker login
	@docker tag ${DOCKERREPO}:latest ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:${VERSION}
	@docker push ${DOCKERREPO}:latest
	@echo ${VERSION} > .latest-version
	@sleep 2
