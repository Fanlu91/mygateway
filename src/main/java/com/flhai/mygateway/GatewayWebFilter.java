package com.flhai.mygateway;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class GatewayWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("===> GatewayWebFilter");
        if (exchange.getRequest().getQueryParams().getFirst("mock") == null) {
            return chain.filter(exchange);
        }
        String mock = """
                {"result": "mocked"}
                """;

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
