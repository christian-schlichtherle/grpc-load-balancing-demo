#!/bin/sh

set -e

./mvnw clean package

for api in Reactor Rx; do
    for app in Client Server; do
        lc_api=`/bin/echo -n $api | tr '[A-Z]' '[a-z]'`
        lc_app=`/bin/echo -n $app | tr '[A-Z]' '[a-z]'`
        docker build -t $lc_api-$lc_app -f - . <<-EOF
            FROM openjdk:8-jre-slim
            WORKDIR /app
            ENTRYPOINT ["java", "-cp", "$lc_api-$lc_app.jar", "ping.${api}${app}"]
            COPY target/grpc-*.jar ./$lc_api-$lc_app.jar
EOF
    done
done
