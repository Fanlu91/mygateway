package com.flhai.mygateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public abstract class AbstractGatewayPlugin implements GatewayPlugin {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        boolean isSupported = support(exchange);
        System.out.println("===> Plugin[" + this.getName() + "], support=" + isSupported);
        return isSupported ? doHandle(exchange) : Mono.empty();
    }

    @Override
    public boolean support(ServerWebExchange exchange) {
        return doSupport(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange);
    public abstract boolean doSupport(ServerWebExchange exchange);
}
