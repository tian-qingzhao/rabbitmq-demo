package com.tqz.rabbitmq.topic;

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

    public static final String TOPIC_EXCHANGE = "topicExchangeTest";

    public static final String QUEUE_NAME1 = "topicQueue1";
    public static final String QUEUE_NAME2 = "topicQueue2";
    public static final String QUEUE_NAME3 = "topicQueue3";
    public static final String QUEUE_NAME4 = "topicQueue4";

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
