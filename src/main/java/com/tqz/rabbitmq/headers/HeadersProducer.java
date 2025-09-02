package com.tqz.rabbitmq.headers;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.tqz.rabbitmq.ConnectionUtil;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求头类型的交换机生产者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 15:35
 */
public class HeadersProducer {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // header模式不需要routingKey来转发，他是根据header里的信息来转发的。比如消费者可以只订阅logLevel=info的消息。
        // 然而，消息发送的API还是需要一个routingKey。
        // 如果使用header模式来转发消息，routingKey可以用来存放其他的业务消息，客户端接收时依然能接收到这个routingKey消息。
        Map<String, Object> headers = new HashMap<>();
        headers.put("loglevel", "info");
        headers.put("buslevel", "product");
        headers.put("syslevel", "admin");

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode());
        builder.priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getPriority());
        builder.headers(headers);

        String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        channel.basicPublish(ConnectionUtil.HEADERS_EXCHANGE, ConnectionUtil.HEADERS_ROUTING_KEY1,
                builder.build(), message.getBytes(StandardCharsets.UTF_8));

        channel.close();
        connection.close();
    }
}
