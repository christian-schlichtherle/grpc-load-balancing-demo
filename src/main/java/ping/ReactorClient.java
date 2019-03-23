package ping;

import io.grpc.Channel;
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
    void run(final Channel channel, final List<Request> requests) {
        final ReactorServiceStub service = newReactorStub(channel);
        Flux
                .fromIterable(requests)
                .parallel()
                .runOn(Schedulers.elastic())
                // `composeGroup` looks like a perfect fit here, but it doesn't properly load balance when used with a
                // Linkerd proxy, so don't use it.
//                .composeGroup(service::streamingPing)
                .flatMap(request -> service.singlePing(request))
                .sequential()
                .groupBy(Response::getServerAddress)
                .flatMap(group -> group.count().map(count -> format("Received %d responses from server %s.", count, group.key())))
                .toStream()
                .forEach(log::info);
        Schedulers.shutdownNow();
    }
}
