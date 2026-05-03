# Stage 1: Cache Gradle dependencies
FROM eclipse-temurin:25-jdk-noble AS dependencies-installer
COPY gradlew build.gradle settings.gradle /sierra/
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.properties /sierra/gradle/wrapper/
COPY sierra-core/build.gradle /sierra/sierra-core/build.gradle
COPY sierra-graphql/build.gradle /sierra/sierra-graphql/build.gradle
COPY asi_interpreter/build.gradle /sierra/asi_interpreter/build.gradle
COPY hivfacts/hivfacts-java/build.gradle /sierra/hivfacts/hivfacts-java/build.gradle
WORKDIR /sierra
RUN /sierra/gradlew dependencies

# Stage 2: Build Sierra WAR (postalign + minimap2 pulled from base image for tests)
FROM eclipse-temurin:25-jdk-noble AS builder
COPY --from=dependencies-installer /sierra/ /sierra/
COPY --from=dependencies-installer /root/ /root/
COPY --from=hivdb/tomcat-with-postalign:latest /usr/local/bin/python3.11 /usr/local/bin/python3.11
COPY --from=hivdb/tomcat-with-postalign:latest /usr/local/lib/python3.11 /usr/local/lib/python3.11
COPY --from=hivdb/tomcat-with-postalign:latest /usr/local/lib/libpython3.11.so.1.0 /usr/local/lib/
RUN ldconfig
COPY --from=hivdb/tomcat-with-postalign:latest /usr/local/bin/postalign /usr/local/bin/postalign
COPY --from=hivdb/tomcat-with-postalign:latest /usr/local/bin/minimap2 /usr/local/bin/minimap2
WORKDIR /sierra
COPY sierra-core /sierra/sierra-core
COPY sierra-graphql /sierra/sierra-graphql
COPY asi_interpreter /sierra/asi_interpreter
COPY hivfacts /sierra/hivfacts
COPY src /sierra/src
RUN /sierra/gradlew assemble
RUN mv build/libs/sierra-*.war build/libs/sierra.war 2>/dev/null

# Stage 3: Runtime image
FROM hivdb/tomcat-with-postalign:latest
COPY --from=builder /sierra/build/libs/sierra.war /usr/share/tomcat/webapps
