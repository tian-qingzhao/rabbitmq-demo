package com.tqz.rabbitmq.springbootmqdemo.consumer1;

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
public class ConsumerRabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("");
        connectionFactory.setHost("106.13.39.231");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("tianqingzhao");
        connectionFactory.setPassword("tqz123456789.");
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

    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("0427 13:29 topicExchange",true,false);
    }

    /**
     * 创建队列
     *
     * @return
     */
    @Bean
    public Queue queue() {
//        return new Queue("springbootQueue",true,false,false,null);
        //队列的名称，是否持久化
        return new Queue("0427 13:17 Queue", true);
    }

    /**
     * 绑定队列跟交换机的关系
     *
     * @return
     */
    @Bean
    public Binding binding() {
        //绑定队列到交换机，路由键的名称为 springbootRabbitMq
        return BindingBuilder.bind(queue()).to(topicExchange()).with("springbootRabbitMq");
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory containerFactory = new TomcatEmbeddedServletContainerFactory(8081);
        return containerFactory;
    }

  /*  @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory());
        return null;
    }*/

    /**
     * 设置手动确认模式
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        // 手动确认
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        containerFactory.setConnectionFactory(connectionFactory());
        // 设置预取的数量，最大值为2500
        containerFactory.setPrefetchCount(2500);
        return containerFactory ;
    }
}
