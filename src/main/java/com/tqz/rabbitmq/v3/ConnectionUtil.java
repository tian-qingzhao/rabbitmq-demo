package com.tqz.rabbitmq.v3;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @Author: tian
 * @Date: 2020/4/24 22:22
 * @Desc: 配置rabbitmq连接
 */
public class ConnectionUtil {

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("tianqingzhao");
        connectionFactory.setPassword("tqz123456789.");
        connectionFactory.setVirtualHost("/");
        return connectionFactory.newConnection();

    }
}
