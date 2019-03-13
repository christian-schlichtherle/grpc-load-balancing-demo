package ping;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;

import java.net.InetAddress;
import java.util.function.Function;
import java.util.logging.Logger;

import static ping.Ping.Request;
import static ping.Ping.Response;

abstract class AbstractServer {

    private static final Logger logger = Logger.getLogger(AbstractServer.class.getName());

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
        logger.info("Listening on " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down");
            server.shutdown();
        }));
        server.awaitTermination();
    }

    abstract BindableService service(Function<Request, Response> response);
}
