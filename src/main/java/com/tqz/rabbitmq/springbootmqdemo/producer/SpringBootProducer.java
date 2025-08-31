package com.tqz.rabbitmq.springbootmqdemo.producer;

import com.alibaba.fastjson.JSON;
import com.tqz.rabbitmq.ConnectionUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:51
 * @Desc:
 */
@Component
public class SpringBootProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        try {
            // 发送失败的时候可以把当前业务的id回调过去
            CorrelationData correlationData = new CorrelationData("订单id");
            Map<String, String> map = new HashMap<>();
            map.put("userName", "tianqingzhao");
            map.put("passWord", message);
            rabbitTemplate.convertAndSend(ConnectionUtil.SPRINGBOOT_EXCHANGE,
                    ConnectionUtil.SPRINGBOOT_ROUTING_KEY1,
                    JSON.toJSONString(map),
                    correlationData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
