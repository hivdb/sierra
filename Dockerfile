FROM hivdb/tomcat-with-nucamino:latest as dependencies-installer
COPY gradlew build.gradle settings.gradle /sierra/
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.properties /sierra/gradle/wrapper/
WORKDIR /sierra
RUN /sierra/gradlew dependencies

FROM hivdb/tomcat-with-nucamino:latest as builder
COPY --from=dependencies-installer /sierra/ /sierra/
COPY --from=dependencies-installer /root/ /root/
WORKDIR /sierra
COPY sierra-core /sierra/sierra-core
COPY sierra-graphql /sierra/sierra-graphql
COPY asi_interpreter /sierra/asi_interpreter 
COPY hivfacts /sierra/hivfacts
COPY src /sierra/src
RUN /sierra/gradlew assemble
RUN mv build/libs/sierra-*.war build/libs/sierra.war 2>/dev/null
ENV MINIMAP2_VERSION=2.17
RUN cd /tmp && \
    curl -sSL https://github.com/lh3/minimap2/releases/download/v2.17/minimap2-${MINIMAP2_VERSION}_x64-linux.tar.bz2 -o minimap2.tar.bz2 && \
    tar jxf minimap2.tar.bz2 && \
    mv minimap2-${MINIMAP2_VERSION}_x64-linux /usr/local/minimap2
COPY docker-payload/postalign_linux-amd64.tar.gz /tmp
RUN cd /tmp && \
    tar zxf postalign_linux-amd64.tar.gz && \
    mv postalign /usr/local/postalign


FROM hivdb/tomcat-with-nucamino:latest
COPY --from=builder /usr/local/minimap2 /usr/local/minimap2
COPY --from=builder /usr/local/postalign /usr/local/postalign
COPY --from=builder /sierra/build/libs/sierra.war /usr/share/tomcat/webapps
RUN cd /usr/local/bin && \
    ln -s ../minimap2/minimap2 && \
    ln -s ../minimap2/k8 && \
    ln -s ../minimap2/paftools.js && \
    echo '#! /bin/sh' > /usr/local/bin/postalign && \
    echo '/usr/local/postalign/postalign $@' >> /usr/local/bin/postalign && \
    chmod +x /usr/local/bin/postalign
