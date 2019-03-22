package ping;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ping.Ping.Request;

abstract class AbstractClient {

    final Logger log = LoggerFactory.getLogger(getClass());

    final void run(final String... args) {
        final int requestCount = args.length > 1 ? Integer.parseInt(args[1]) : 100;
        final List<Request> requests = IntStream
                .range(0, requestCount)
                .mapToObj(AbstractClient::request)
                .collect(Collectors.toList());
        String target = args.length > 0 ? args[0] : "localhost";
        if (!target.contains(":")) {
            target += ":50505";
        }
        final ManagedChannel channel = ManagedChannelBuilder
                .forTarget(target)
                .usePlaintext()
                .build();
        final CountDownLatch
                shutdownInitiated = new CountDownLatch(1),
                shutdownCompleted = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down.");
            shutdownInitiated.countDown();
            try {
                shutdownCompleted.await(6, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }));
        try {
            try {
                do {
                    run(channel, requests);
                } while (!shutdownInitiated.await(2, TimeUnit.SECONDS));
            } finally {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException ignored) {
        } finally {
            shutdownCompleted.countDown();
        }
    }

    private static Request request(int i) {
        return Request.newBuilder().setMessage(Integer.toString(i)).build();
    }

    abstract void run(Channel channel, List<Request> requests);
}
