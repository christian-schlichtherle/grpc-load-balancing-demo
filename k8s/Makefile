up: kong-up servers-up clients-up

kong-up:
	kubectl apply --filename postgres.yaml
	kubectl apply --filename kong_migration_postgres.yaml
	kubectl apply --filename kong_postgres.yaml

servers-up: build
	kubectl apply --filename reactor-server.yaml
	kubectl apply --filename rx-server.yaml

clients-up: build
	kubectl apply --filename reactor-client.yaml
	kubectl apply --filename rx-client.yaml

build:
	docker build --file Dockerfile.kong-config --build-arg API=reactor --tag kong-config:reactor-server .
	docker build --file Dockerfile.kong-config --build-arg API=rx      --tag kong-config:rx-server      .

down: clients-down servers-down kong-down

clients-down:
	kubectl delete --ignore-not-found --filename reactor-client.yaml
	kubectl delete --ignore-not-found --filename rx-client.yaml

servers-down:
	kubectl delete --ignore-not-found --filename reactor-server.yaml
	kubectl delete --ignore-not-found --filename rx-server.yaml

kong-down:
	kubectl delete --ignore-not-found --filename kong_postgres.yaml
	kubectl delete --ignore-not-found --filename kong_migration_postgres.yaml
	kubectl delete --ignore-not-found --filename postgres.yaml
