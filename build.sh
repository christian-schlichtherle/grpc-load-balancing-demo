#!/bin/bash

set -e

./mvnw clean package

DIRNAME=$(dirname $0)
for API in Reactor Rx; do
    for APP in Client Server; do
        LC_API=$(tr '[A-Z]' '[a-z]' <<<$API)
        LC_APP=$(tr '[A-Z]' '[a-z]' <<<$APP)
        docker build -t $LC_API-$LC_APP -f - "$DIRNAME" <<-EOF
            FROM openjdk:8-jre-slim
            WORKDIR /app
            ENTRYPOINT ["java", "-cp", "demo.jar", "ping.$API$APP"]
            COPY target/grpc-*.jar ./demo.jar
EOF
    done
done
