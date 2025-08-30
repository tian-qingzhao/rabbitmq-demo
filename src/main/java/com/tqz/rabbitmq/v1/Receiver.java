package com.tqz.rabbitmq.v1;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: 田
 * @date: 2019/10/30 20:46
 * @Desc: 消息接受者
 */
@Component
public class Receiver {

    /**
     *@author 田
     *@date 2019/10/30 20:49
     *@Desc: 蚕蛹消息监听机制接受消息
     */
    @RabbitListener(queues = "hello-queue")
    public void consumer(String msg){
        System.out.println("receiver:"+ msg);
    }
}