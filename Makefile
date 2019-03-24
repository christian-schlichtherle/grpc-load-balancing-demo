images:
	./mvnw package
	docker build -t grpc-load-balancing-demo -f Dockerfile target
