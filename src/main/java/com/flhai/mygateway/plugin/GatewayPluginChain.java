package com.flhai.mygateway.plugin;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 多个plugin共同对单一请求进行处理
 */
public abstract class GatewayPluginChain {
    public abstract Mono<Void> handle(ServerWebExchange exchange);
}
