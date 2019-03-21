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

## Set-Up

### Building The Docker Images 

    $ ./build.sh

### Deploying The Stack

To set-up a Docker swarm:

    $ docker swarm init

To deploy the Docker stack:

    $ docker stack deploy --compose-file docker-compose.yml demo

### Checking the Client Logs

To watch the log of the Reactor-client:

    $ docker logs --follow $(docker ps --filter name=reactor-client --format '{{.ID}}')
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

To check the log of the Rx-server:

    $ docker logs --follow $(docker ps --filter name=rx-client --format '{{.ID}}')

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

    $ docker stack rm demo
