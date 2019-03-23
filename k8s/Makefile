up: linkerd-up servers-up clients-up

down: clients-down servers-down linkerd-down

linkerd-up:
	linkerd install | kubectl apply --filename -

linkerd-down:
	linkerd install | kubectl delete --filename -

servers-up:
	linkerd inject reactor-server.yaml | kubectl apply --filename -
	linkerd inject rx-server.yaml | kubectl apply --filename -

servers-down:
	kubectl delete --ignore-not-found --filename reactor-server.yaml
	kubectl delete --ignore-not-found --filename rx-server.yaml

clients-up:
	linkerd inject reactor-client.yaml | kubectl apply --filename -
	linkerd inject rx-client.yaml | kubectl apply --filename -

clients-down:
	kubectl delete --ignore-not-found --filename reactor-client.yaml
	kubectl delete --ignore-not-found --filename rx-client.yaml
