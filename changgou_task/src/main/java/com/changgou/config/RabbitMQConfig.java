package com.changgou.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_TASK = "order_task";

    @Bean
    public Queue queue() {
        return new Queue(ORDER_TASK);
    }
}
