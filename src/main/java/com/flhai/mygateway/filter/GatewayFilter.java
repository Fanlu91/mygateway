package com.flhai.mygateway.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange);
}
