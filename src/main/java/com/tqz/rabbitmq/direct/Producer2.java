package com.tqz.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 1:21
 */
public class Producer2 {

    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();
        //创建一个通道，一个连接可以创建多个通道
        Channel channel = connection.createChannel();


//        channel.queueDeclare("test-queue-888", true, false, false, null);

        channel.exchangeDeclare("test-exchange-666", "direct", false, true, null);

        channel.close();
        connection.close();

    }
}
