package com.tqz.rabbitmq.headers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求头类型的交换机消费者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 15:36
 */
public class HeadersConsumer {

    public static void main(String[] args) throws Exception {
        Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("loglevel", "info");
        headers.put("buslevel", "product");
//		headers.put("syslevel", "admin");

        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(ConnectionUtil.HEADERS_QUEUE_NAME1, true, false, false, null);

        channel.queueBind(ConnectionUtil.HEADERS_QUEUE_NAME1, ConnectionUtil.HEADERS_EXCHANGE,
                ConnectionUtil.HEADERS_ROUTING_KEY1, headers);

        String consumerTag = channel.basicConsume(ConnectionUtil.HEADERS_QUEUE_NAME1, true,
                new MyDefaultConsumer(channel));
        System.out.println("consumerTag > " + consumerTag);
    }
}
