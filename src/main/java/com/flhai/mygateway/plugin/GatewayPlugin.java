package com.flhai.mygateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface GatewayPlugin {
    String GATEWAY_PREFIX = "/ga";
    void start();
    void stop();

    String getName();
    boolean support(ServerWebExchange exchange);
    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);
}
