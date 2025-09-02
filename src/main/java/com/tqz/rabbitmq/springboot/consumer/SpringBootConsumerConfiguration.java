package com.tqz.rabbitmq.springboot.consumer;

import com.tqz.rabbitmq.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:39
 * @Desc:
 */
@Slf4j
@Configuration
public class SpringBootConsumerConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(ConnectionUtil.RABBITMQ_HOST);
        connectionFactory.setPort(ConnectionUtil.RABBITMQ_PORT);
        connectionFactory.setUsername(ConnectionUtil.RABBITMQ_USERNAME);
        connectionFactory.setPassword(ConnectionUtil.RABBITMQ_PASSWORD);
        connectionFactory.setVirtualHost(ConnectionUtil.RABBITMQ_VIRTUAL_HOST);
        // 开启发送方确认模式
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        // 把配置注册到模板中，如果使用了properties或者yml配置设置上述配置的，这里可以省略，springboot会自动配置该模板。

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());

        return rabbitTemplate;
    }

    @Bean
    public TopicExchange topicExchange() {
        Map<String, Object> map = new HashMap<>();
        return new TopicExchange(ConnectionUtil.SPRINGBOOT_EXCHANGE1, true, false, map);
    }

    @Bean
    public Queue queue() {
        return new Queue(ConnectionUtil.SPRINGBOOT_QUEUE_NAME1, true, false, false, null);
    }

    @Bean
    public Binding binding() {
        // 绑定队列到交换机
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ConnectionUtil.SPRINGBOOT_ROUTING_KEY1);
    }

    @Bean
    public TopicExchange topicExchange2() {
        Map<String, Object> map = new HashMap<>();
        return new TopicExchange(ConnectionUtil.SPRINGBOOT_EXCHANGE2, true, false, map);
    }

    @Bean
    public Queue streamQueue() {
        Map<String, Object> params = new HashMap<>();
        params.put("x-queue-type", "stream");
        // maximum stream size: 20 GB
        params.put("x-max-length-bytes", 20_000_000_000L);
        // size of segment files: 100 MB
        params.put("x-stream-max-segment-size-bytes", 100_000_000);
        return new Queue(ConnectionUtil.SPRINGBOOT_QUEUE_NAME2,
                true, false, false, params);
    }

    @Bean
    public Binding bindingStream() {
        // 绑定stream队列到交换机
        return BindingBuilder.bind(streamQueue()).to(topicExchange2()).with(ConnectionUtil.SPRINGBOOT_ROUTING_KEY2);
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory(8081);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置是否重新入队被拒绝的消息，这里设置为false，因为我们将在代码中处理确认或拒绝。
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(2);
        return factory;
    }
}
