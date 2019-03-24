all: package image

clean package:
	./mvnw $@

image:
	docker build -t grpc-load-balancing-demo -f Dockerfile target
