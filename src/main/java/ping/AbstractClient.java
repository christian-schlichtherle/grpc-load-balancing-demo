package ping;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.console;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.interrupted;
import static java.util.Optional.ofNullable;
import static ping.Ping.Request;

abstract class AbstractClient {

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
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        do {
            run(channel, requests);

            // Required to make the client aware of new servers if the "round_robin" client-side load-balancing policy
            // is used and the number of servers is SCALED UP:
            channel.enterIdle();
        } while (!interrupted() && repeat());
        try {
            channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            currentThread().interrupt();
        }
    }

    private static Request request(int i) {
        return Request.newBuilder().setMessage(Integer.toString(i)).build();
    }

    abstract void run(ManagedChannel channel, List<Request> requests);

    private static boolean repeat() {
        String input;
        do {
            input = ofNullable(console())
                    .flatMap(c -> ofNullable(c.printf("Repeat? [YES|no] ").readLine()))
                    .orElse("n")
                    .trim();
        } while (!input.matches("(?i)y(es)?|no?|"));
        return !input.matches("(?i)no?");
    }
}
