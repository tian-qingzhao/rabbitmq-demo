package com.tqz.rabbitmq.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

/**
 * 没有交换机的消费者2.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 14:25
 */
public class Consumer2 {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(ConnectionUtil.WORK_QUEUE_NAME, true, false, false, null);
        //每个worker同时最多只处理一个消息
        channel.basicQos(1);
        channel.basicConsume(ConnectionUtil.WORK_QUEUE_NAME, new MyDefaultConsumer(channel));
    }
}
