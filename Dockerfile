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

FROM hivdb/tomcat-with-nucamino:latest as postalign-builder
RUN apt-get -q update && apt-get install -qqy python3.9-full python3.9-dev gcc
ADD https://bootstrap.pypa.io/get-pip.py /tmp/get-pip.py
RUN python3.9 /tmp/get-pip.py
RUN pip install cython==0.29.32
ARG POSTALIGN_VERSION=5ea22aa50c7ccfcc06e2871daa2a6aeaeb17ae2d
RUN pip install https://github.com/hivdb/post-align/archive/${POSTALIGN_VERSION}.zip

FROM hivdb/tomcat-with-nucamino:latest
ENV CATALINA_OPTS "-Xms1024M -Xmx6144M"
RUN apt-get -q update && apt-get install -qqy python3.9
COPY --from=builder /usr/local/minimap2 /usr/local/minimap2
COPY --from=builder /sierra/build/libs/sierra.war /usr/share/tomcat/webapps
COPY --from=postalign-builder /usr/local/lib/python3.9 /usr/local/lib/python3.9
COPY --from=postalign-builder /usr/local/bin/postalign /usr/local/bin/postalign
RUN sed -i 's/<Context>/<Context privileged="true">/' /usr/share/tomcat/conf/context.xml
RUN cd /usr/local/bin && \
    ln -s ../minimap2/minimap2 && \
    ln -s ../minimap2/k8 && \
    ln -s ../minimap2/paftools.js
