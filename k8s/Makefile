up: servers-up clients-up

servers-up:
	linkerd inject reactor-server.yaml | kubectl apply --filename -
	linkerd inject      rx-server.yaml | kubectl apply --filename -

clients-up:
	linkerd inject reactor-client.yaml | kubectl apply --filename -
	linkerd inject      rx-client.yaml | kubectl apply --filename -

down: clients-down servers-down

clients-down:
	kubectl delete --ignore-not-found --filename reactor-client.yaml
	kubectl delete --ignore-not-found --filename      rx-client.yaml

servers-down:
	kubectl delete --ignore-not-found --filename reactor-server.yaml
	kubectl delete --ignore-not-found --filename      rx-server.yaml
