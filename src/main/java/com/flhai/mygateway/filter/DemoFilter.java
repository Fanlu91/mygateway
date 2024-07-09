package com.flhai.mygateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component("demoFilter")
public class DemoFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        System.out.println("===> DemoFilter");
        exchange.getRequest().getHeaders().toSingleValueMap().forEach((key, value) -> {
            System.out.println("--- Header[" + key + "]=" + value);
        });
        return Mono.empty();
    }
}
