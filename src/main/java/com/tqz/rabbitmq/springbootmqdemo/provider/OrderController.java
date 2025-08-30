package com.tqz.rabbitmq.springbootmqdemo.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: tian
 * @Date: 2020/4/25 23:54
 * @Desc:
 */
@RestController
public class OrderController {

    @Autowired
    private Provider provider;

    @RequestMapping("/order")
    public Object order(String exchangeName,String routingKey,String message){
//        System.out.println("exchangeName:" + exchangeName);
//        System.out.println("routingKey:" + routingKey);
//        System.out.println("message:" + message);
        for (int i=0;i<20000;i++){
            provider.sendMessage(exchangeName,routingKey,message+i);
        }
        return "下单成功";
    }
}
