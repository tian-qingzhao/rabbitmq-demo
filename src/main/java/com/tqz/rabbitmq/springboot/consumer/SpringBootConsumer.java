package com.tqz.rabbitmq.springboot.consumer;

import com.rabbitmq.client.Channel;
import com.tqz.rabbitmq.ConnectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author: tian
 * @Date: 2020/4/26 0:24
 * @Desc:
 */
@Slf4j
@Component
public class SpringBootConsumer {

    @RabbitListener(queues = ConnectionUtil.SPRINGBOOT_QUEUE_NAME1)
    public void listenerCreateOrder(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        processMessage(deliveryTag, channel);

        log.info("消费者监听创建订单的消息：{}，消息的deliveryTag：{}", msg, deliveryTag);
    }

    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue(value = ConnectionUtil.SPRINGBOOT_QUEUE_NAME2,
                    durable = "true",
                    arguments = {
                            @Argument(name = "x-queue-type", value = "stream"),
                            @Argument(name = "x-max-length-bytes", value = "20000000000", type =
                                    "java.lang.Long"),
                            @Argument(name = "x-stream-max-segment-size-bytes", value = "100000000"
                                    , type = "java.lang.Long"),
                            @Argument(name = "x-stream-offset", value = "last")
                    }),
            exchange = @Exchange(
                    value = ConnectionUtil.SPRINGBOOT_EXCHANGE2,
                    durable = "true",
                    type = ExchangeTypes.TOPIC),
            key = ConnectionUtil.SPRINGBOOT_ROUTING_KEY2)}
    )
    public void listenerCreateUser(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String correlationId = new String(message.getMessageProperties().getCorrelationId(), StandardCharsets.UTF_8);

        channel.basicQos(1);

        processMessage(deliveryTag, channel);

        log.info("消费者监听创建用户的消息：{}，消息的deliveryTag：{}，correlationId：{}",
                msg, deliveryTag, correlationId);
    }

    private void processMessage(long deliveryTag, Channel channel) throws IOException {
        // 下订单成功才进行确认消息，要不然就让消息重回队列
        if (orderIsSuccess()) {
            // 手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识
            // 第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            channel.basicAck(deliveryTag, false);
        } else {
            // 批量退回，第一个参数消息的唯一标识，第二个参数是否批量退回，第三个是参数是否重回队列
            channel.basicNack(deliveryTag, false, true);
            // 单条退回
            // channel.basicReject(deliveryTag, true);
        }
    }

    /**
     * 订单是否下成功，模拟真实下订单业务（下订单成功返回true，失败返回false）
     */
    public boolean orderIsSuccess() {
        return true;
    }
}
