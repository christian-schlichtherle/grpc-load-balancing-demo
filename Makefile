images: target/grpc-load-balancing-demo-*.jar
	bin/build-docker-images

target/grpc-load-balancing-demo-*.jar:
	bin/mvnw package
