# Stage 1: Build postalign
FROM python:3.11-slim AS postalign-builder
RUN apt-get -q update && apt-get install -qqy --no-install-recommends gcc python3-dev
RUN pip install --no-cache-dir cython==0.29.35
ARG POSTALIGN_VERSION=8e2ee118261987208c17add6cef5c1270e325a4c
RUN pip install --no-cache-dir https://github.com/hivdb/post-align/archive/${POSTALIGN_VERSION}.zip

# Stage 2: Build minimap2 (prebuilt for amd64, source build for arm64)
# Compiling from source under QEMU x86_64 emulation triggers gcc segfaults,
# so we use the upstream prebuilt amd64 binary and compile from source only for arm64.
FROM debian:bookworm-slim AS minimap2-builder
# NOTE: MiniMap2 >= 2.18 handles --score-N=0 differently, so we stick with 2.17 for now
ARG MINIMAP2_VERSION=2.17
ARG TARGETARCH
RUN apt-get -q update && apt-get install -qqy --no-install-recommends curl ca-certificates bzip2 && \
    if [ "${TARGETARCH}" = "amd64" ]; then \
        cd /tmp && \
        curl -sSL https://github.com/lh3/minimap2/releases/download/v${MINIMAP2_VERSION}/minimap2-${MINIMAP2_VERSION}_x64-linux.tar.bz2 -o minimap2.tar.bz2 && \
        tar jxf minimap2.tar.bz2 && \
        cp minimap2-${MINIMAP2_VERSION}_x64-linux/minimap2 /usr/local/bin/minimap2; \
    else \
        apt-get install -qqy --no-install-recommends make gcc zlib1g-dev libbz2-dev && \
        cd /tmp && \
        curl -sSL https://github.com/lh3/minimap2/archive/v${MINIMAP2_VERSION}.tar.gz -o minimap2.tar.gz && \
        tar zxf minimap2.tar.gz && \
        cd minimap2-${MINIMAP2_VERSION} && \
        make arm_neon=1 aarch64=1 && \
        cp minimap2 /usr/local/bin/minimap2; \
    fi

# Stage 3: Runtime base
FROM eclipse-temurin:25-jre-noble

# Install Tomcat
ARG TOMCAT_VERSION=10.1.54
RUN apt-get -q update && \
    apt-get install -qqy --no-install-recommends curl && \
    cd /tmp && \
    curl -sSL https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o tomcat.tar.gz && \
    tar zxf tomcat.tar.gz && \
    mkdir -p /usr/share/tomcat10 && \
    mv apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat10/ && \
    ln -s /usr/share/tomcat10/apache-tomcat-${TOMCAT_VERSION} /usr/share/tomcat && \
    rm -rf /usr/share/tomcat/webapps/* && \
    chmod +x /usr/share/tomcat/bin/*.sh && \
    cp /usr/share/tomcat/conf/web.xml /root/web.dev.xml && \
    cp /usr/share/tomcat/conf/web.xml /root/web.prod.xml && \
    sed -i 's/<Context>/<Context privileged="true">/' /usr/share/tomcat/conf/context.xml && \
    apt-get remove -qqy curl && apt-get autoremove -qqy && \
    rm -rf /tmp/* /var/lib/apt/lists/*

ENV CATALINA_OPTS="-Xms1024M -Xmx3584M -XX:+UseG1GC -XX:MaxHeapFreeRatio=30 -XX:MinHeapFreeRatio=10"
ENV LC_ALL=C.UTF-8
ENV LANG=C.UTF-8

# Copy minimap2
COPY --from=minimap2-builder /usr/local/bin/minimap2 /usr/local/bin/minimap2

# Copy postalign and its Python 3.11 runtime
COPY --from=postalign-builder /usr/local/bin/python3.11 /usr/local/bin/python3.11
COPY --from=postalign-builder /usr/local/lib/python3.11 /usr/local/lib/python3.11
COPY --from=postalign-builder /usr/local/lib/libpython3.11.so.1.0 /usr/local/lib/
RUN ldconfig
COPY --from=postalign-builder /usr/local/bin/postalign /usr/local/bin/postalign

EXPOSE 8080
COPY src/hivdb-tomcat-entrypoint.sh /usr/bin/entrypoint
ENTRYPOINT ["/usr/bin/entrypoint"]
