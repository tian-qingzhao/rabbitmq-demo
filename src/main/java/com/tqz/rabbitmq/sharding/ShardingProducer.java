package com.tqz.rabbitmq.sharding;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;

import java.util.Date;

/**
 * 分片存储消息的生产者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/9/1 22:42
 */
public class ShardingProducer {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 发送者只管往exchange里发消息，而不用关心具体发到哪些queue里
        channel.exchangeDeclare(ConnectionUtil.SHARDING_EXCHANGE, "x-modulus-hash");

        for (int i = 0; i < 3000; i++) {
            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN) + " " + i;
            channel.basicPublish(ConnectionUtil.SHARDING_EXCHANGE, String.valueOf(i), null, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
