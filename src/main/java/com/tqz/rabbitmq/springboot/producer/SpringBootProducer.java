package com.tqz.rabbitmq.springboot.producer;

import com.alibaba.fastjson.JSON;
import com.tqz.rabbitmq.ConnectionUtil;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:51
 * @Desc:
 */
@Component
public class SpringBootProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendCreateOrder(String message) {
        sendMessage0(message, ConnectionUtil.SPRINGBOOT_EXCHANGE1,
                ConnectionUtil.SPRINGBOOT_ROUTING_KEY1);
    }

    public void sendCreateUser(String message) {
        sendMessage0(message, ConnectionUtil.SPRINGBOOT_EXCHANGE2,
                ConnectionUtil.SPRINGBOOT_ROUTING_KEY2);
    }

    private void sendMessage0(String msg, String exchange, String routingKey) {
        try {
            // 发送失败的时候可以把当前业务的id回调过去
            Map<String, String> map = new HashMap<>();
            map.put("userName", "tianqingzhao");
            map.put("passWord", msg);

            String id = UUID.randomUUID().toString();

            MessagePostProcessor processor = key -> {
                key.getMessageProperties().setCorrelationId(id.getBytes());
                return key;
            };

            rabbitTemplate.convertAndSend(exchange,
                    routingKey,
                    JSON.toJSONString(map),
                    processor,
                    new CorrelationData(id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
