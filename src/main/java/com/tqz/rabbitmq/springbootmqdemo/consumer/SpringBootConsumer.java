package com.tqz.rabbitmq.springbootmqdemo.consumer;

import com.rabbitmq.client.Channel;
import com.tqz.rabbitmq.ConnectionUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Author: tian
 * @Date: 2020/4/26 0:24
 * @Desc:
 */
@Component
public class SpringBootConsumer {

    @RabbitListener(queues = ConnectionUtil.SPRINGBOOT_QUEUE_NAME1, containerFactory =
            "simpleRabbitListenerContainerFactory")
    public void get2(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        // 下订单成功才进行确认消息，要不然就让消息重回队列
        if (orderIsSuccess()) {
            // 手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识
            // 第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            // 批量退回，第一个参数消息的唯一标识，第二个参数是否批量退回，第三个是参数是否重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            // 单条退回
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

        System.out.println("消费者拿到的消息：" + new String(message.getBody(), StandardCharsets.UTF_8));
    }

    /**
     * 订单是否下成功，模拟真实下订单业务（下订单成功返回true，失败返回false）
     *
     * @return
     */
    public boolean orderIsSuccess() {
        return true;
    }
}
