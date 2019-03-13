package ping;

import io.grpc.BindableService;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.function.Function;

import static ping.Ping.Request;
import static ping.Ping.Response;
import static ping.RxServiceGrpc.ServiceImplBase;

public class RxServer extends AbstractServer {

    public static void main(String... args) throws Exception {
        new RxServer().run(args);
    }

    @Override
    BindableService service(Function<Request, Response> response) {
        return new ServiceImplBase() {

            @Override
            public Single<Response> singlePing(Single<Request> request) {
                return request.map(response::apply);
            }

            @Override
            public Flowable<Response> streamingPing(Flowable<Request> request) {
                return request.map(response::apply);
            }
        };
    }
}
