FROM ubuntu:20.04
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8
ENV CATALINA_OPTS "-Xms512M -Xmx1024M"
RUN apt-get update -qy && \
    apt-get install -qqy software-properties-common curl && \
    /usr/bin/add-apt-repository ppa:linuxuprising/java && \
    apt-get update -qy && \
    echo debconf shared/accepted-oracle-license-v1-3 select true | debconf-set-selections && \
    echo debconf shared/accepted-oracle-license-v1-3 seen true | debconf-set-selections && \
    apt-get install -qqy oracle-java17-installer oracle-java17-set-default
ARG TOMCAT_VERSION=10.0.14
RUN cd /tmp && \
    curl -sSL https://www.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o tomcat10.tar.gz && \
    tar zxf tomcat10.tar.gz && \
    mkdir -p /usr/share/tomcat10 && \
    mv /tmp/apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat10 && \
    ln -s /usr/share/tomcat10/apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat && \
    rm -rf /usr/share/tomcat/webapps/* && \
    chmod +x /usr/share/tomcat/bin/*.sh && \
    cp /usr/share/tomcat/conf/web.xml /root/web.dev.xml && \
    cp /usr/share/tomcat/conf/web.xml /root/web.prod.xml && \
    cp /root/web.prod.xml /usr/share/tomcat/conf/web.xml && \
    rm -rf /tmp/*
ENV NUCAMINO_VERSION=0.2.0-beta.2
# RUN curl -L https://github.com/hivdb/NucAmino/releases/download/${NUCAMINO_VERSION}/nucamino-linux-amd64 -o /usr/bin/nucamino
ADD nucamino-linux-amd64 /usr/bin/nucamino
EXPOSE 8080
ADD src/hivdb-tomcat-entrypoint.sh /usr/bin/entrypoint
RUN chmod +x /usr/bin/entrypoint /usr/bin/nucamino
ENTRYPOINT ["/usr/bin/entrypoint"]
