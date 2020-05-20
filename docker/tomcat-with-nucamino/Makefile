build:
	@docker pull ubuntu:18.04
	@docker build -t hivdb/tomcat-with-nucamino:latest .

force-build:
	@docker build --no-cache -t hivdb/tomcat-with-nucamino:latest .

inspect:
	@docker rm -f inspect-tomcat 2>/dev/null || true
	@docker run --name=inspect-tomcat --rm -it hivdb/tomcat-with-nucamino:latest inspect || true

release: build
	docker login
	docker push hivdb/tomcat-with-nucamino:latest
