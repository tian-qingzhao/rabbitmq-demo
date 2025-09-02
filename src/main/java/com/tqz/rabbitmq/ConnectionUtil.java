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

    public static final String RABBITMQ_HOST = "127.0.0.1";
    public static final Integer RABBITMQ_PORT = 5672;
    public static final String RABBITMQ_USERNAME = "guest";
    public static final String RABBITMQ_PASSWORD = "guest";
    public static final String RABBITMQ_VIRTUAL_HOST = "/";

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

    public static final String DEAD_LETTER_QUEUE_NORMAL_EXCHANGE = "deadLetterQueueNormalExchangeTest";
    public static final String DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME1 = "deadLetterQueueNormalQueue1";
    public static final String DEAD_LETTER_QUEUE_NORMAL_QUEUE_NAME2 = "deadLetterQueueNormalQueue2";
    public static final String DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY1 = "deadLetterQueueNormalRoutingKey1";
    public static final String DEAD_LETTER_QUEUE_NORMAL_ROUTING_KEY2 = "deadLetterQueueNormalRoutingKey2";
    public static final String DEAD_LETTER_QUEUE_DEAD_EXCHANGE = "deadLetterQueueDeadExchangeTest";
    public static final String DEAD_LETTER_QUEUE_DEAD_QUEUE_NAME1 = "deadLetterQueueDeadQueue1";
    public static final String DEAD_LETTER_QUEUE_DEAD_ROUTING_KEY1 = "deadLetterQueueDeadRoutingKey1";

    public static final String SHARDING_EXCHANGE = "shardingExchangeTest";

    public static final String SPRINGBOOT_EXCHANGE1 = "springbootExchangeTest1";
    public static final String SPRINGBOOT_EXCHANGE2 = "springbootExchangeTest2";
    public static final String SPRINGBOOT_QUEUE_NAME1 = "springbootQueue1";
    public static final String SPRINGBOOT_QUEUE_NAME2 = "springbootQueue2";
    public static final String SPRINGBOOT_ROUTING_KEY1 = "springbootRoutingKey1";
    public static final String SPRINGBOOT_ROUTING_KEY2 = "springbootRoutingKey2";

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBITMQ_HOST);
        connectionFactory.setPort(RABBITMQ_PORT);
        connectionFactory.setUsername(RABBITMQ_USERNAME);
        connectionFactory.setPassword(RABBITMQ_PASSWORD);
        connectionFactory.setVirtualHost(RABBITMQ_VIRTUAL_HOST);
        return connectionFactory.newConnection();

    }
}
