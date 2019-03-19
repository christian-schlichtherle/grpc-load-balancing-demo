package ping;

import io.grpc.ManagedChannel;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static java.lang.String.format;
import static ping.Ping.Request;
import static ping.Ping.Response;
import static ping.ReactorServiceGrpc.ReactorServiceStub;
import static ping.ReactorServiceGrpc.newReactorStub;

public class ReactorClient extends AbstractClient {

    public static void main(String... args) {
        new ReactorClient().run(args);
    }

    @Override
    void run(final ManagedChannel channel, final List<Request> requests) {
        final ReactorServiceStub service = newReactorStub(channel);
        Flux
                .fromIterable(requests)
                .parallel()
                .runOn(Schedulers.elastic())
                // This could be `.composeGroup(service::streamingPing)`, but then the load balancing doesn't work:
                .map(request -> service.singlePing(request).block())
                .sequential()
                .groupBy(Response::getServerAddress)
                .flatMap(group -> group.count().map(count -> format("Received %d responses from server %s.", count, group.key())))
                .toStream()
                .forEach(log::info);
        Schedulers.shutdownNow();
    }
}
