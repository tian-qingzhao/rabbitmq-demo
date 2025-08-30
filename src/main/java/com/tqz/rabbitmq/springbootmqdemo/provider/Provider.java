package com.tqz.rabbitmq.springbootmqdemo.provider;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:51
 * @Desc:
 */
@Component
public class Provider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @PostConstruct
//    public void init(){
//        rabbitTemplate.setConfirmCallback();
//    }

    public void sendMessage(String exchangeName,String routingKey,String message){
        try {
            CorrelationData correlationData = new CorrelationData("订单id"); //发送失败的时候可以把当前业务的id回调过去
            Map map = new HashMap();
            map.put("userName","tianqingzhao");
            map.put("passWord",message);
            rabbitTemplate.convertAndSend(exchangeName,routingKey, JSON.toJSONString(map),correlationData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
