up: linkerd-up servers-up clients-up

linkerd-up:
	kubectl apply --filename linkerd.yaml

servers-up:
#	kubectl apply --filename reactor-server.yaml
	kubectl apply --filename rx-server.yaml

clients-up:
#	kubectl apply --filename reactor-client.yaml
	kubectl apply --filename rx-client.yaml

down: clients-down servers-down linkerd-down

clients-down:
#	kubectl delete --ignore-not-found --filename reactor-client.yaml
	kubectl delete --ignore-not-found --filename rx-client.yaml

servers-down:
#	kubectl delete --ignore-not-found --filename reactor-server.yaml
	kubectl delete --ignore-not-found --filename rx-server.yaml

linkerd-down:
	kubectl delete --ignore-not-found --filename linkerd.yaml
