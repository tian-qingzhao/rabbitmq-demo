package com.tqz.rabbitmq.springbootmqdemo.consumer3;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: tian
 * @Date: 2020/4/27 1:55
 * @Desc: 配置rabbitmq连接
 */
public class ConnectionUtil {

    public static final String FANOUT_EXCHANGE = "fanoutExchange-springboot";

    public static final String QUEUE_NAME1 = "0427 13:17 Queue";

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("106.13.39.231");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("tianqingzhao");
        connectionFactory.setPassword("tqz123456789.");
        connectionFactory.setVirtualHost("/");
        return connectionFactory.newConnection();

    }
}
