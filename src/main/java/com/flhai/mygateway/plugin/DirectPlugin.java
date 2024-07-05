package com.flhai.mygateway.plugin;

import com.flhai.mygateway.AbstractGatewayPlugin;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component("directPlugin")
public class DirectPlugin extends AbstractGatewayPlugin {
    private static final String NAME = "direct";
    private String prefix = GATEWAY_PREFIX + "/" + NAME + "/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println("===> DirectPlugin");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("com.flhai.mygateway.version", "1.0");
        exchange.getResponse().getHeaders().add("com.flhai.mygateway.plugin", NAME);

        if (backend == null || backend.isEmpty()) {
            return requestBody.flatMap(dataBuffer -> exchange.getResponse().writeWith(Mono.just(dataBuffer))).then();
        }

        // 5. 通过webclient发送请求
        WebClient webClient = WebClient.create(backend);
        Mono<ResponseEntity<String>> responseEntityMono = webClient.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);
        // 6. 通过responseEntityMono获取响应
        Mono<String> bodyMono = responseEntityMono.map(ResponseEntity::getBody);
//        bodyMono.subscribe(System.out::println);
        // 7. 组装报文

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
