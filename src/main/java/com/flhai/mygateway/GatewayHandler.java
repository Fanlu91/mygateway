package com.flhai.mygateway;

import com.flhai.myrpc.core.api.LoadBalancer;
import com.flhai.myrpc.core.cluster.RandomLoadBalancer;
import com.flhai.myrpc.core.meta.InstanceMeta;
import com.flhai.myrpc.core.meta.ServiceMeta;
import com.flhai.myrpc.core.registry.RegistryCenter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
//@Component
public class GatewayHandler {
    @Autowired
    RegistryCenter registryCenter;

    /**
     * http://localhost:8888/gw/com.flhai.myrpc.demo.api.UserService
     *
     * @param request
     * @return
     */
    Mono<ServerResponse> handle(ServerRequest request) {
        // 1. 获取服务名
        String serviceName = request.path().substring(4);
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
        Mono<String> stringMono = request.bodyToMono(String.class);

        return stringMono.flatMap(s -> {
            return invoke(url, s);
        });
    }

    @NotNull
    private static Mono<ServerResponse> invoke(String url, String s) {
        // 5. 通过webclient发送请求
        WebClient webClient = WebClient.create(url);
        Mono<ResponseEntity<String>> responseEntityMono = webClient.post()
                .header("Content-Type", "application/json")
                .bodyValue(s).retrieve().toEntity(String.class);
        // 6. 通过responseEntityMono获取响应
        Mono<String> bodyMono = responseEntityMono.map(ResponseEntity::getBody);
        bodyMono.subscribe(System.out::println);
        // 7. 组装报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("com.flhai.mygateway.version", "1.0")
                .body(bodyMono, String.class);
    }

}
