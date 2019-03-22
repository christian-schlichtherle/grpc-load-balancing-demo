# gRPC Load Balancing Demo

This project demonstrates how to implement a load-balancing strategy for gRPC.

The project contains a simple, ping-like streaming service ([`ping.proto`](src/main/proto/ping/ping.proto)), and 
implementations of the client and server using
[Reactor-gRPC](https://github.com/salesforce/reactive-grpc/tree/master/reactor) and 
[RxgRPC](https://github.com/salesforce/reactive-grpc/tree/master/rx-java): 

<table>
    <tr>
        <th></th>
        <th>Reactor-gRPC</th>
        <th>RxgRPC</th>
    </tr>
    <tr>
        <th>Client</th>
        <td><a href="src/main/java/ping/ReactorClient.java">ReactorClient</a></td>
        <td><a href="src/main/java/ping/RxClient.java">RxClient</a></td>
    </tr>
    <tr>
        <th>Server</th>
        <td><a href="src/main/java/ping/ReactorServer.java">ReactorServer</a></td>
        <td><a href="src/main/java/ping/RxServer.java">RxServer</a></td>
    </tr>
</table>

## Building The Docker Images 

    $ ./build.sh

## Deploying To A Docker Swarm

This section shows how to deploy the client and server images to a Docker Swarm.
Alternatively, you can also deploy to a Kubernetes cluster instead ([see below](#deploying-to-a-kubernetes-cluster)). 

### Prerequisites

+ Pre-built Docker images for the clients and servers ([see above](#building-the-docker-images)).
+ A Docker Swarm - one node is enough.

### Deploying The Stack

To deploy the Docker stack:

    $ docker stack deploy --compose-file swarm/docker-compose.yml demo

To check the deployment:

    $ docker service ls

Repeat until the REPLICAS numbers match.

### Checking the Client Logs

To watch the log of the Reactor-client:

    $ docker service logs --follow demo_reactor-client
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.5.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.6.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 28 responses from server 10.0.17.7.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.8.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 47 responses from server 10.0.17.5.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 18 responses from server 10.0.17.6.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 17 responses from server 10.0.17.7.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 18 responses from server 10.0.17.8.

The client sends 100 requests to the four server instances (on as many threads as the client has CPUs).
The output is a list of tuples which show the IP address of each responding server and the number of requests/responses 
it has processed.

To watch the log of the Rx-client:

    $ docker service logs --follow demo_rx-client

### Scaling the Servers

While the clients are running, you can down-scale or up-scale the number of server instances.
When you decrease the number of server instances, the clients will immediately recognize the change:

    $ docker service update --replicas 2 demo_reactor-server
    $ docker service update --replicas 2 demo_rx-server

When you increase the number of server instances however, the client will only _eventually_ recognize the change:

    $ docker service update --replicas 4 demo_reactor-server
    $ docker service update --replicas 4 demo_rx-server

When following this example, the logging output will change accordingly.

### Removing the Stack

To remove the Docker stack:

    $ docker stack rm demo

## Deploying To A Kubernetes Cluster

This section shows how to deploy the client and server images to a Kubernetes cluster.
Alternatively, you can also deploy to a Docker swarm instead ([see above](#deploying-to-a-docker-swarm)). 


### Prerequisites

+ Pre-built Docker images for the clients and servers ([see above](#building-the-docker-images)).
+ A Kubernetes cluster - one node is enough. If not already available, use Docker Desktop.

### Deploying The Stack

To deploy the stack:

    $ make --directory k8s

To check the deployment:

    $ kubectl get deployment

Repeat until the AVAILABLE numbers match the DESIRED numbers.

### Checking the Client Logs

To watch the log of the Reactor-client:

    $ kubectl logs --follow deployment/reactor-client
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.5.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.6.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 28 responses from server 10.0.17.7.
    09:55:12.072 [main] INFO  ping.ReactorClient - Received 24 responses from server 10.0.17.8.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 47 responses from server 10.0.17.5.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 18 responses from server 10.0.17.6.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 17 responses from server 10.0.17.7.
    09:55:14.226 [main] INFO  ping.ReactorClient - Received 18 responses from server 10.0.17.8.

The client sends 100 requests to the four server instances (on as many threads as the client has CPUs).
The output is a list of tuples which show the IP address of each responding server and the number of requests/responses 
it has processed.

To watch the log of the Rx-client:

    $ kubectl logs --follow deployment/rx-client 

### Scaling the Servers

While the clients are running, you can down-scale or up-scale the number of server instances.
When you decrease the number of server instances, the clients will immediately recognize the change:

    $ kubectl scale --replicas 2 deployment/reactor-server
    $ kubectl scale --replicas 2 deployment/rx-server

When you increase the number of server instances however, the client will only _eventually_ recognize the change:

    $ kubectl scale --replicas 4 deployment/reactor-server
    $ kubectl scale --replicas 4 deployment/rx-server

When following this example, the logging output will change accordingly.

### Removing the Stack

To remove the Docker stack:

    $ make --directory k8s down
