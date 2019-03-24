all: package images

package:
	./mvnw package

clean:
	./mvnw clean

images:
	docker build -t grpc-load-balancing-demo -f Dockerfile target
