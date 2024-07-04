package com.flhai.mygateway;

import com.flhai.myrpc.core.registry.RegistryCenter;
import com.flhai.myrpc.core.registry.my.MyRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;


@Configuration
public class GatewayConfig {
    @Bean
    public RegistryCenter registryCenter() {
        return new MyRegistryCenter();
    }

    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context){
        return args -> {
            // 向SimpleUrlHandlerMapping中额外增加一个mapping
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("增加了/ga/** mapping");
        };
    }
}
