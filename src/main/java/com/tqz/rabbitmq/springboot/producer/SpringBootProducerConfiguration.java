package com.tqz.rabbitmq.springboot.producer;

import com.alibaba.fastjson.JSON;
import com.tqz.rabbitmq.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
@Slf4j
@Configuration
public class SpringBootProducerConfiguration {

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

        // 确认回调通知
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // 消息的标识
                log.info("生产者发送消息的信息 corrletionData:{}，ack:{}，cause:{}",
                        correlationData, ack, cause);
            }
        });

        // 开启失败回调
        rabbitTemplate.setMandatory(true);
        // 回调通知
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String
                    routingKey) {
                log.info("生产者发送失败的消息内容：{}replyCode:{}，replyText:{}，exchange:{}，routingKey:{}",
                        new String(message.getBody()), replyCode, replyText, exchange, routingKey);
            }
        });

        // 设置转换器
        rabbitTemplate.setMessageConverter(new MessageConverter() {
            // 发送消息的时候转换
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
                messageProperties.setContentType("text/xml");
                messageProperties.setContentEncoding("UTF-8");
                return new Message(JSON.toJSONBytes(o), messageProperties);
            }

            // 接收消息的时候转换
            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                return null;
            }
        });
        return rabbitTemplate;
    }

    @Bean
    public TopicExchange topicExchange() {
        Map<String, Object> map = new HashMap<>();
        // 备用交换机，如果当前交换机不存在，就会使用备用交换机，如果备用交换机也不存在，就会回调失败
        // map.put("alternate-exchange", "backUpExchange");
        return new TopicExchange(ConnectionUtil.SPRINGBOOT_EXCHANGE1, true, false, map);
    }

    @Bean
    public Queue classicQueue() {
        return new Queue(ConnectionUtil.SPRINGBOOT_QUEUE_NAME1, true, false, false, null);
    }

    @Bean
    public Binding bindingClassic() {
        // 绑定classic队列到交换机
        return BindingBuilder.bind(classicQueue()).to(topicExchange()).with(ConnectionUtil.SPRINGBOOT_ROUTING_KEY1);
    }

    @Bean
    public TopicExchange topicExchange2() {
        Map<String, Object> map = new HashMap<>();
        // 备用交换机，如果当前交换机不存在，就会使用备用交换机，如果备用交换机也不存在，就会回调失败
        // map.put("alternate-exchange", "backUpExchange");
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

}
