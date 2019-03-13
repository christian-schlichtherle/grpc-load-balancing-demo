package ping;

import io.grpc.ManagedChannel;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

import static ping.Ping.Request;
import static ping.Ping.Response;
import static ping.RxServiceGrpc.RxServiceStub;
import static ping.RxServiceGrpc.newRxStub;

public class RxClient extends AbstractClient {

    public static void main(String... args) {
        new RxClient().run(args);
    }

    @Override
    void run(final ManagedChannel channel, final List<Request> requests) {
        final RxServiceStub service = newRxStub(channel);
        Schedulers.start();
        Flowable
                .fromIterable(requests)
                .parallel()
                .runOn(Schedulers.io())
                // This could be `.flatMap(request -> service.streamingPing(Flowable.just(request)))`, but then the load balancing wouldn't work:
                .map(request -> service.singlePing(request).blockingGet())
                .sequential()
                .groupBy(Response::getServerAddress)
                .flatMap(group -> group.count().map(count -> String.format("[%s,%d]", group.getKey(), count)).toFlowable())
                .blockingSubscribe(System.out::println);
        Schedulers.shutdown();
    }
}
