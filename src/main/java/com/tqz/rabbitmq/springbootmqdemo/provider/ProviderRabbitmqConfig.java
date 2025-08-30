package com.tqz.rabbitmq.springbootmqdemo.provider;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:39
 * @Desc:
 */
@Configuration
public class ProviderRabbitmqConfig {

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("106.13.39.231");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("tianqingzhao");
        connectionFactory.setPassword("tqz123456789.");
        //开启发送方确认模式
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    /**
     * 把配置注册到模板中，如果使用了properties或者yml配置设置上述配置的，这里可以省略，springboot会自动配置该模板。
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        //确认回调通知
    /*    rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("corrletionData:" + correlationData); //消息的标识
                System.out.println("ack:" + ack); //有没有成功发送到rabbitmq
                System.out.println("cause:" +cause); //失败的原因
            }
        });*/
        //开启失败回调
        rabbitTemplate.setMandatory(true);
        //回调通知
     /*   rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                //消息加上消息的配置
                System.out.println(new String(message.getBody()));
                //状态码
                System.out.println(replyCode);
                //失败的信息 
                System.out.println(replyText);
                //发送到哪个交换机
                System.out.println(exchange);
                //发送到哪个路由键
                System.out.println(routingKey);
            }
        });*/
        rabbitTemplate.setConnectionFactory(connectionFactory());
        //设置转换器
     /*   rabbitTemplate.setMessageConverter(new MessageConverter() {
            //发送消息的时候转换
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
                messageProperties.setContentType("text/xml");
                messageProperties.setContentEncoding("UTF-8");
                Message message = new Message(JSON.toJSONBytes(o),messageProperties);
                return message;
            }

            //接收消息的时候转换
            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                return null;
            }
        });*/
        return rabbitTemplate;
    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange(){
        Map<String,Object> map = new HashMap<String,Object>();
        //备用交换机，如果当前交换机不存在，就会使用备用交换机，如果备用交换机也不存在，就会回调失败
        map.put("alternate-exchange","backUpExchange");
        return new TopicExchange("0427 13:29 topicExchange",true,false,map);
    }

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanoutExchange-springboot",true,false);
    }

    /**
     * 创建队列
     * @return
     */
    @Bean
    public Queue queue(){
//        return new Queue("springbootQueue",true,false,false,null);
        //队列的名称，是否持久化
        return new Queue("0427 13:17 Queue",true);
    }

    /**
     * 绑定队列跟交换机的关系
     * @return
     */
    @Bean
    public Binding binding(){
        //绑定队列到交换机，路由键的名称为 springbootRabbitMq
//        return BindingBuilder.bind(queue()).to(topicExchange()).with("springbootRabbitMq");
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }
}
