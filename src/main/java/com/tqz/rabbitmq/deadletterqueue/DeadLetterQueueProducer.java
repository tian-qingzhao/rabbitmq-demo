package com.tqz.rabbitmq.deadletterqueue;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.tqz.rabbitmq.ConnectionUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列生产者
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/9/1 18:02
 */
public class DeadLetterQueueProducer {

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();

        Channel channel1 = connection.createChannel(1);

        Channel channel2 = connection.createChannel(2);

        channel1.exchangeDeclare(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_EXCHANGE,
                "direct", true, false, null);

        Map<String, Object> params1 = new HashMap<>();
        // 死信交换机。创建queue时参数arguments设置了x-dead-letter-routing-key和x-dead-letter-exchange，
        // 会在x-message-ttl时间到期后把消息放到x-dead-letter-routing-key和x-dead-letter-exchange指定的队列中达到延迟队列的目的。
        params1.put("x-dead-letter-exchange", ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_EXCHANGE);
        // 死信路由键。这里的routing-key也可以是队列名称，当消息过期后会转发到这个exchange对应的routing-key，达到延时队列效
        params1.put("x-dead-letter-routing-key", ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_ROUTING_KEY1);

        channel1.queueDeclare(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME1,
                true, false, false, params1);
        channel1.queueBind(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME1,
                ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_EXCHANGE,
                ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY1);

        Map<String, Object> params2 = new HashMap<>();
        // 消息存活时间创建queue时设置该参数可指定消息在该queue中待多久
        // 这里的优先级比下面的AMQP.BasicProperties#expiration参数优先级高
        params2.put("x-message-ttl", 20_000);
        // 死信交换机。创建queue时参数arguments设置了x-dead-letter-routing-key和x-dead-letter-exchange，
        // 会在x-message-ttl时间到期后把消息放到x-dead-letter-routing-key和x-dead-letter-exchange指定的队列中达到延迟队列的目的。
        params2.put("x-dead-letter-exchange", ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_EXCHANGE);
        // 死信路由键。这里的routing-key也可以是队列名称，当消息过期后会转发到这个exchange对应的routing-key，达到延时队列效
        params2.put("x-dead-letter-routing-key", ConnectionUtil.DEAD_LETTER_QUEUE_DEAD_ROUTING_KEY1);
        channel2.queueDeclare(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME2,
                true, false, false, params2);
        channel2.queueBind(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME2,
                ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_EXCHANGE,
                ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY2);

        AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                .contentType("application/json") // 内容类型
                .priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode()) // 优先级（0-9）
                .deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode()) // 2=持久化（存硬盘）
                .expiration("60000") // 消息60秒过期
                .build();

        for (int i = 1; i <= 10; i++) {
            // 偶数往路由键1发送，奇数往路由键2发送
            if (i % 2 == 0) {
                // 该路由键关联的队列有消费者进行消费，但是消费者进入了Nack，所以会立马进入死信队列

                String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN) + "偶数：" + i;
                System.out.println("往队列1发送的消息：" + message);
                channel1.basicPublish(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_EXCHANGE,
                        ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY1,
                        props,
                        message.getBytes());
            } else {
                // 该路由键关联的队列没有消费进行消费，所以过了x-message-ttl时间之后，会进入到死信队列

                String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN) + "奇数：" + i;
                System.out.println("往队列2发送的消息：" + message);
                channel2.basicPublish(ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_EXCHANGE,
                        ConnectionUtil.DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY2, props,
                        message.getBytes());
            }

            Thread.sleep(800);
        }
    }
}
