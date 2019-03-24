up: servers-up clients-up

down: clients-down servers-down

servers-up:
	istioctl kube-inject --filename reactor-server.yaml | kubectl apply --filename -
	istioctl kube-inject --filename rx-server.yaml | kubectl apply --filename -

servers-down:
	istioctl kube-inject --filename reactor-server.yaml | kubectl delete --ignore-not-found --filename -
	istioctl kube-inject --filename rx-server.yaml | kubectl delete --ignore-not-found --filename -

clients-up:
	istioctl kube-inject --filename reactor-client.yaml | kubectl apply --filename -
	istioctl kube-inject --filename rx-client.yaml | kubectl apply --filename -

clients-down:
	istioctl kube-inject --filename reactor-client.yaml | kubectl delete --ignore-not-found --filename -
	istioctl kube-inject --filename rx-client.yaml | kubectl delete --ignore-not-found --filename -
