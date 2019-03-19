up: kong-up servers-up clients-up

kong-up:
	kubectl apply --filename k8s/postgres.yaml
	kubectl apply --filename k8s/kong_migration_postgres.yaml
	kubectl apply --filename k8s/kong_postgres.yaml

servers-up: build
	kubectl apply --filename k8s/reactor-server.yaml
	kubectl apply --filename k8s/rx-server.yaml

clients-up: build
	kubectl apply --filename k8s/reactor-client.yaml
	kubectl apply --filename k8s/rx-client.yaml

build:
	./build.sh
	docker build --file k8s/Dockerfile.kong-config --build-arg API=reactor --tag kong-config:reactor-server k8s
	docker build --file k8s/Dockerfile.kong-config --build-arg API=rx      --tag kong-config:rx-server      k8s

down: clients-down servers-down kong-down

clients-down:
	kubectl delete --ignore-not-found --filename k8s/reactor-client.yaml
	kubectl delete --ignore-not-found --filename k8s/rx-client.yaml

servers-down:
	kubectl delete --ignore-not-found --filename k8s/reactor-server.yaml
	kubectl delete --ignore-not-found --filename k8s/rx-server.yaml

kong-down:
	kubectl delete --ignore-not-found --filename k8s/kong_postgres.yaml
	kubectl delete --ignore-not-found --filename k8s/kong_migration_postgres.yaml
	kubectl delete --ignore-not-found --filename k8s/postgres.yaml
