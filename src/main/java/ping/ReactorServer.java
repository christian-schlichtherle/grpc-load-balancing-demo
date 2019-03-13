package ping;

import io.grpc.BindableService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static ping.Ping.Request;
import static ping.Ping.Response;
import static ping.ReactorServiceGrpc.ServiceImplBase;

public class ReactorServer extends AbstractServer {

    public static void main(String... args) throws Exception {
        new ReactorServer().run(args);
    }

    @Override
    BindableService service(Function<Request, Response> response) {
        return new ServiceImplBase() {

            @Override
            public Mono<Response> singlePing(Mono<Request> request) {
                return request.map(response);
            }

            @Override
            public Flux<Response> streamingPing(Flux<Request> request) {
                return request.map(response);
            }
        };
    }
}
