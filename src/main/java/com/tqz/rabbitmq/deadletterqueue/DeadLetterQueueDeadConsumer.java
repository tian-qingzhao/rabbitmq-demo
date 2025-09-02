package com.tqz.rabbitmq.deadletterqueue;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.tqz.rabbitmq.ConnectionUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 死信队列消费所有的死信消息的消费者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/9/1 18:23
 */
public class DeadLetterQueueDeadConsumer {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        // 创建一个通道，一个连接可以创建多个通道
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_EXCHANGE,
                "direct", true, false, null);
        channel.queueDeclare(ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_QUEUE_NAME1,
                true, false, false, null);
        channel.queueBind(ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_QUEUE_NAME1,
                ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_EXCHANGE,
                ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_ROUTING_KEY1);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();

                System.out.println(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN) +
                        "死信队列消费所有死信的消费者获取到的消息：" +
                        " consumerTag：" + consumerTag + ",deliveryTag" + "：" + deliveryTag +
                        " contentType：" + properties.getContentType() +
                        "," + "body：" + new String(body, StandardCharsets.UTF_8));

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        //服务端推模式，接受消息，开始消费
        channel.basicConsume(ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_QUEUE_NAME1, defaultConsumer);
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
