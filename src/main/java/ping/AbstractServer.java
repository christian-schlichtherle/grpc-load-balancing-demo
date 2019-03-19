package ping;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.function.Function;

import static ping.Ping.Request;
import static ping.Ping.Response;

abstract class AbstractServer {

    final Logger log = LoggerFactory.getLogger(getClass());

    final void run(final String... args) throws Exception {
        // TODO: This isn't necessarily exactly the same IP address as for which a Request was received:
        final String address = InetAddress.getLocalHost().getHostAddress();
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : 50505;
        final BindableService service = service(request -> Response
                .newBuilder()
                .setMessage(request.getMessage())
                .setServerAddress(address)
                .setServerPort(port)
                .build());
        final io.grpc.Server server = ServerBuilder
                .forPort(port)
                .addService(service)
                .build()
                .start();
        log.info("Listening on port {}.", server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down.");
            server.shutdown();
        }));
        server.awaitTermination();
    }

    abstract BindableService service(Function<Request, Response> response);
}
