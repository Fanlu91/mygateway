package com.flhai.mygateway.plugin;

import com.flhai.mygateway.plugin.GatewayPlugin;
import com.flhai.mygateway.plugin.GatewayPluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public class DefaultGatewayPluginChain extends GatewayPluginChain {

    List<GatewayPlugin> plugins;
    int index = 0;

    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        // 惰性处理
        return Mono.defer(() -> {
            if (index < plugins.size()) {
                GatewayPlugin plugin = plugins.get(index);
                index++;
                return plugin.handle(exchange, this);
            } else {
                return Mono.empty();
            }
        });
    }
}
