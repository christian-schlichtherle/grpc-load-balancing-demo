FROM openjdk:8-jre-slim
WORKDIR /app
ENTRYPOINT ["java", "-cp", "grpc-load-balancing-demo.jar"]
COPY grpc-load-balancing-demo-*.jar ./grpc-load-balancing-demo.jar
