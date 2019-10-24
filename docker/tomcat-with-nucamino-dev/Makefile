build:
	@docker pull ubuntu:16.04
	@docker build -t hivdb/tomcat-with-nucamino-dev .

force-build:
	@docker build --no-cache -t hivdb/tomcat-with-nucamino-dev .

inspect:
	@docker rm -f inspect-tomcat 2>/dev/null || true
	@docker run --name=inspect-tomcat --rm -it hivdb/tomcat-with-nucamino-dev inspect || true
