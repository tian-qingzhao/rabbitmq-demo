package com.tqz.rabbitmq;

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

    public static final String FANOUT_EXCHANGE = "fanoutExchangeTest";
    public static final String FANOUT_QUEUE_NAME1 = "fanoutQueue1";
    public static final String FANOUT_QUEUE_NAME2 = "fanoutQueue2";
    public static final String FANOUT_QUEUE_NAME3 = "fanoutQueue3";
    public static final String FANOUT_QUEUE_NAME4 = "fanoutQueue4";

    public static final String DIRECT_EXCHANGE = "directExchangeTest";
    public static final String DIRECT_QUEUE_NAME1 = "directQueue1";
    public static final String DIRECT_QUEUE_NAME2 = "directQueue2";
    public static final String DIRECT_QUEUE_NAME3 = "directQueue3";
    public static final String DIRECT_QUEUE_NAME4 = "directQueue4";

    public static final String TOPIC_EXCHANGE = "topicExchangeTest";
    public static final String TOPIC_QUEUE_NAME1 = "topicQueue1";
    public static final String TOPIC_QUEUE_NAME2 = "topicQueue2";
    public static final String TOPIC_QUEUE_NAME3 = "topicQueue3";
    public static final String TOPIC_QUEUE_NAME4 = "topicQueue4";

    public static final String HEADERS_EXCHANGE = "headersExchangeTest";
    public static final String HEADERS_QUEUE_NAME1 = "headersQueue1";
    public static final String HEADERS_ROUTING_KEY1 = "headersRoutingKey1";

    public static final String WORK_QUEUE_NAME = "non_exchange_queue";

    public static final String PUBLISH_CONFIRMS_EXCHANGE = "publishConfirmsExchangeTest";
    public static final String PUBLISH_CONFIRMS_QUEUE_NAME1 = "publishConfirmsQueue1";
    public static final String PUBLISH_CONFIRMS_ROUTING_KEY1 = "publishConfirmsRoutingKey1";

    public static final String STREAM_EXCHANGE = "streamExchangeTest";
    public static final String STREAM_QUEUE_NAME1 = "streamQueue1";
    public static final String STREAM_ROUTING_KEY1 = "streamRoutingKey1";

    public static final String SPRINGBOOT_EXCHANGE = "springbootExchangeTest";
    public static final String SPRINGBOOT_QUEUE_NAME1 = "springbootQueue1";
    public static final String SPRINGBOOT_ROUTING_KEY1 = "springbootRoutingKey1";

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory.newConnection();

    }
}
