VERSION=$(shell date -u +"%Y%m%d%H%M%S")

.build: $(shell find `greadlink -f ../..` `greadlink -f ../../hiv-genotyper` -type f -not -path "./.git*" -a -not -path "*.class" -a -not -path "*/.DS_Store" -a -not -path "*/.gradle/*" -a -not -path "*/build/*" -a -not -path "*/target/*" -a -not -path "*/.settings/*" -a -not -path "*.log" -a -not -path "*/__output/*" -a -not -path "docker/*" | sed 's#\([| ]\)#\\\1#g')
	@mkdir -p target
	@cd ../..; ./gradlew assemble
	@mv ../../WebApplications/build/libs/WebApplications-*.war target/WebApplications.war
	@touch .build

build: .build
	@docker build -t hivdb/sierra .

force-build: .build
	@docker build --no-cache -t hivdb/sierra .

release: build
	@docker login
	@docker tag hivdb/sierra:latest hivdb/sierra:${VERSION}
	@docker push hivdb/sierra:${VERSION}
	@docker push hivdb/sierra:latest
	@echo ${VERSION} > .latest-version
	@sleep 2
