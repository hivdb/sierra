FROM ubuntu:16.04

ENV TOMCAT_VERSION=8.5.37
ENV CATALINA_OPTS "-Xms512M -Xmx1024M"
RUN apt-get update -qy && \
    apt-get install -qqy software-properties-common curl && \
    /usr/bin/add-apt-repository ppa:webupd8team/java && \
    apt-get update -qy && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections && \
    apt-get install -qqy oracle-java8-installer oracle-java8-set-default && \
    cd /tmp && \
    curl http://www.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o tomcat8.tar.gz && \
    tar zxf tomcat8.tar.gz && \
    mkdir -p /usr/share/tomcat8 && \
    mv /tmp/apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat8 && \
    ln -s /usr/share/tomcat8/apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat && \
    rm -rf /usr/share/tomcat/webapps/* && \
    chmod +x /usr/share/tomcat/bin/*.sh && \
    cp /usr/share/tomcat/conf/web.xml /root/web.dev.xml && \
    cp /usr/share/tomcat/conf/web.xml /root/web.prod.xml && \
    cp /root/web.prod.xml /usr/share/tomcat/conf/web.xml
ENV NUCAMINO_VERSION=0.2.0-beta.2
# RUN curl -L https://github.com/hivdb/NucAmino/releases/download/${NUCAMINO_VERSION}/nucamino-linux-amd64 -o /usr/bin/nucamino
ADD nucamino-linux-amd64 /usr/bin/nucamino
EXPOSE 8080
ADD src/hivdb-tomcat-entrypoint.sh /usr/bin/entrypoint
RUN chmod +x /usr/bin/entrypoint /usr/bin/nucamino
ENTRYPOINT ["/usr/bin/entrypoint"]
