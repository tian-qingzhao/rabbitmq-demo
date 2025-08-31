package com.tqz.rabbitmq.springbootmqdemo.consumer;

import com.tqz.rabbitmq.ConnectionUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:39
 * @Desc:
 */
@Configuration
public class ConsumerRabbitmqConfiguration {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("");
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    /**
     * 把配置注册到模板中，如果使用了properties或者yml配置设置上述配置的，这里可以省略，springboot会自动配置该模板。
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(ConnectionUtil.SPRINGBOOT_EXCHANGE, true, false);
    }

    @Bean
    public Queue queue() {
        return new Queue(ConnectionUtil.SPRINGBOOT_QUEUE_NAME1, true, false, false, null);
    }

    @Bean
    public Binding binding() {
        //绑定队列到交换机
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ConnectionUtil.SPRINGBOOT_ROUTING_KEY1);
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        return new TomcatEmbeddedServletContainerFactory(8081);
    }

    /**
     * 设置手动确认模式
     *
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        // 手动确认
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactory.setConnectionFactory(connectionFactory());
        // 设置预取的数量，最大值为2500
        containerFactory.setPrefetchCount(2500);
        return containerFactory;
    }
}
