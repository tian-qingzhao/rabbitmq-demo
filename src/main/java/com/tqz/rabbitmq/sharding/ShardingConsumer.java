package com.tqz.rabbitmq.sharding;

import com.rabbitmq.client.*;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 分片存储消息的消费者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/9/1 22:42
 */
public class ShardingConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 队列的名称必须填写成交换机的名称
        channel.queueDeclare(ConnectionUtil.SHARDING_EXCHANGE,
                false, false, false, null);

        // 三个分片就需要消费三次。
        // sharding插件的实现原理就是将basicConsume方法绑定到分片队列中连接最少的一个队列上。
        String consumerFlag1 = channel.basicConsume(ConnectionUtil.SHARDING_EXCHANGE,
                false, new MyDefaultConsumer(channel));
        System.out.println("consumer1:" + consumerFlag1);

        String consumerFlag2 = channel.basicConsume(ConnectionUtil.SHARDING_EXCHANGE,
                false, new MyDefaultConsumer(channel));
        System.out.println("consumer2:" + consumerFlag2);

        String consumerFlag3 = channel.basicConsume(ConnectionUtil.SHARDING_EXCHANGE,
                false, new MyDefaultConsumer(channel));
        System.out.println("consumer3:" + consumerFlag3);
    }
}
