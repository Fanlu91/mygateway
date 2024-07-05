package com.flhai.mygateway.web.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Deprecated
//@Component
public class HelloHandler {
    Mono<ServerResponse> handleHello(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("hello mygateway"), String.class);
    }
}
