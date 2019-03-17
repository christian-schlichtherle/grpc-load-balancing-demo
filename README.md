# gRPC Round Robin Load Balancing Demo

This project demonstrates how to apply a DNS Round Robin load-balancing policy for gRPC.

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

### Deploying The Servers

To set-up a Docker swarm:

    $ docker swarm init

To set-up an attachable overlay network for testing:

    $ docker network create --driver overlay --attachable test-net 

To create the servers for both Reactor-gRPC and RxgRPC:

    $ docker service create --replicas 4 --network test-net --endpoint-mode dnsrr --name reactor-server reactor-server
    $ docker service create --replicas 4 --network test-net --endpoint-mode dnsrr --name rx-server rx-server

Note that the containers must be put on the overlay network (`test-net` in this example) and the endpoint-mode must be 
`dnsrr`.

### Running the Clients

To run the Reactor-client and let it talk to the Reactor-servers:

```bash
$ docker run --interactive --tty --rm --network test-net reactor-client reactor-server
[DEBUG] (main) Using Console logging
[10.0.0.17,24]
[10.0.0.16,24]
[10.0.0.15,24]
[10.0.0.14,28]
Repeat? [YES|no] no
```

This example sends 100 requests to the four server instances (on as many threads as the client has CPUs).
The output is a list of tuples which show the IP address of each responding server and the number of requests/responses 
it has processed.

To let the same client talk to the Rx-servers instead:

```bash
$ docker run --interactive --tty --rm --network test-net reactor-client rx-server
[DEBUG] (main) Using Console logging
[10.0.0.19,28]
[10.0.0.22,24]
[10.0.0.21,24]
[10.0.0.20,24]
Repeat? [YES|no] 
```

Note the different IP addresses.

You can also repeat the same experiment with the Rx-client instead.
To let it talk to the Reactor-servers:

    $ docker run --interactive --tty --rm --network test-net rx-client reactor-server

To let it talk to the Rx-servers instead:

    $ docker run --interactive --tty --rm --network test-net rx-client rx-server

The behavior is the same as before, so the output is not shown here.

### Scaling the Servers

While the clients are running, you can down-scale or up-scale the number of server instances.
When you decrease the number of server instances, the clients will immediately recognize the change:

    $ docker service update --replicas 2 reactor-server
    $ docker service update --replicas 2 rx-server

When you increase the number of server instances however, the client will only _eventually_ recognize the change:

    $ docker service update --replicas 4 reactor-server
    $ docker service update --replicas 4 rx-server

When following this example, a client session may look like this:

```bash
$ docker run --interactive --tty --rm --network test-net rx-client rx-server
[10.0.0.19,24]
[10.0.0.22,24]
[10.0.0.21,24]
[10.0.0.20,28]
Repeat? [YES|no] 
[10.0.0.19,52]
[10.0.0.20,48]
Repeat? [YES|no] 
[10.0.0.19,24]
[10.0.0.35,24]
[10.0.0.34,28]
[10.0.0.20,24]
Repeat? [YES|no] no
```

Note the change of the IP addresses after scaling up.
