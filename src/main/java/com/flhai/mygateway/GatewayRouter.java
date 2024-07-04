package com.flhai.mygateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * 基于函数式编程的路由
 */
@Component
public class GatewayRouter {
//    @Autowired
//    HelloHandler helloHandler;
//
//    @Autowired
//    GatewayHandler gatewayHandler;
//
//    /**
//     * functions like handler mapping
//     * <p>
//     * RequestPredicate : GET("/hello")
//     * HandlerFunction :
//     *
//     * @return
//     */
//    @Bean
//    public RouterFunction<?> userRouterFunction() {
//        return route(GET("/hello"), helloHandler::handleHello);
//    }
//
//    @Bean
//    public RouterFunction<?> gatewayRouterFunction() {
//        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handle);
//    }
}
