up: servers-up clients-up

servers-up: build
	kubectl apply --filename k8s/reactor-server.yaml
	kubectl apply --filename k8s/rx-server.yaml

clients-up: build
	kubectl apply --filename k8s/reactor-client.yaml
	kubectl apply --filename k8s/rx-client.yaml

build:
	./build.sh

down: clients-down servers-down

clients-down:
	kubectl delete --ignore-not-found --filename k8s/reactor-client.yaml
	kubectl delete --ignore-not-found --filename k8s/rx-client.yaml

servers-down:
	kubectl delete --ignore-not-found --filename k8s/reactor-server.yaml
	kubectl delete --ignore-not-found --filename k8s/rx-server.yaml
