package com.flhai.mygateway.web.handler;

import com.flhai.mygateway.GatewayPlugin;
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
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 相比router处理，灵活性更强
 * <p>
 * 可以满足串联的需求
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    // 将符合条件的 beans 注册到list中
    @Autowired
    List<GatewayPlugin> plugins;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println("===> GatewayWebHandler");
        if (plugins != null) {
            for (GatewayPlugin plugin : plugins) {
                if (plugin.support(exchange)) {
                    return plugin.handle(exchange);
                }
            }
        }
        System.out.println("no supported plugin found");
        String result = """
                {
                    result: "no supported plugin found"
                }
                """;
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));

    }


//    @Autowired
//    RegistryCenter registryCenter;
//
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange) {
//        // 1. 获取服务名
//        String serviceName = exchange.getRequest().getPath().value().substring(4);
//        // 2. 通过rc获取活着的服务实例
//        ServiceMeta serviceMeta = ServiceMeta.builder()
//                .name(serviceName)
//                .app("app1")
//                .env("dev")
//                .namespace("public")
//                .build();
//        List<InstanceMeta> instanceMetas = registryCenter.fetchAll(serviceMeta);
//        // 3. 简化处理，取第一个实例
//        LoadBalancer<InstanceMeta> loadBalancer = new RandomLoadBalancer();
//        String url = loadBalancer.choose(instanceMetas).toUrl();
//        // 4. 获取请求报文
//        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
//
//        // 5. 通过webclient发送请求
//        WebClient webClient = WebClient.create(url);
//        Mono<ResponseEntity<String>> responseEntityMono = webClient.post()
//                .header("Content-Type", "application/json")
//                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
//        // 6. 通过responseEntityMono获取响应
//        Mono<String> bodyMono = responseEntityMono.map(ResponseEntity::getBody);
////        bodyMono.subscribe(System.out::println);
//        // 7. 组装报文
//        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
//        exchange.getResponse().getHeaders().add("com.flhai.mygateway.version", "1.0");
//        return bodyMono.flatMap(s -> {
//            return exchange.getResponse().writeWith(
//                    Mono.just(exchange.getResponse().bufferFactory().wrap(s.getBytes())));
//        });
//
//    }
//    @Override
//    public Mono<Void> handle(ServerWebExchange exchange) {
//        return exchange.getResponse().writeWith(
//                Mono.just(exchange.getResponse().bufferFactory().wrap("hello mygateway web handler".getBytes())));
//    }

}
