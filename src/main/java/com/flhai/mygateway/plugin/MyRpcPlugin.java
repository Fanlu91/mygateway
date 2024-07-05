package com.flhai.mygateway.plugin;

import com.flhai.mygateway.AbstractGatewayPlugin;
import com.flhai.myrpc.core.api.LoadBalancer;
import com.flhai.myrpc.core.cluster.RandomLoadBalancer;
import com.flhai.myrpc.core.meta.InstanceMeta;
import com.flhai.myrpc.core.meta.ServiceMeta;
import com.flhai.myrpc.core.registry.RegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component("myRpcPlugin")
public class MyRpcPlugin extends AbstractGatewayPlugin {
    @Autowired
    RegistryCenter registryCenter;
    private static final String NAME = "myrpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println("===> MyRpcPlugin");
        // 1. 获取服务名
        String serviceName = exchange.getRequest().getPath().value().substring(prefix.length());
        // 2. 通过rc获取活着的服务实例
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(serviceName)
                .app("app1")
                .env("dev")
                .namespace("public")
                .build();
        List<InstanceMeta> instanceMetas = registryCenter.fetchAll(serviceMeta);
        // 3. 简化处理，取第一个实例
        LoadBalancer<InstanceMeta> loadBalancer = new RandomLoadBalancer();
        String url = loadBalancer.choose(instanceMetas).toUrl();
        // 4. 获取请求报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5. 通过webclient发送请求
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> responseEntityMono = webClient.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 通过responseEntityMono获取响应
        Mono<String> bodyMono = responseEntityMono.map(ResponseEntity::getBody);
//        bodyMono.subscribe(System.out::println);
        // 7. 组装报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("com.flhai.mygateway.version", "1.0");
        exchange.getResponse().getHeaders().add("com.flhai.mygateway.plugin", NAME);
        return bodyMono.flatMap(s -> {
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(s.getBytes())));
        });
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
