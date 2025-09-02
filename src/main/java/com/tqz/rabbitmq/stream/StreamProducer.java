package com.tqz.rabbitmq.stream;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * stream类型的队列生产者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 17:39
 */
public class StreamProducer {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明队列会在服务端自动创建。
        Map<String, Object> params = new HashMap<>();
        // 声明Stream队列
        params.put("x-queue-type", "stream");
        // maximum stream size: 20 GB，在磁盘的总空间，超过该大小，会自动删除前面的
        params.put("x-max-length-bytes", 20_000_000_000L);
        // size of segment files: 100 MB，每个小文件的大小
        params.put("x-stream-max-segment-size-bytes", 100_000_000);
        channel.exchangeDeclare(ConnectionUtil.STREAM_EXCHANGE, "topic");
        channel.queueDeclare(ConnectionUtil.STREAM_QUEUE_NAME1, true, false, false, params);

        // 把交换机给队列绑定上
        // 生产者如果不想声明队列和队列绑定到交换机的步骤，也可以在消费者去实现，
        // 生产者只需要定义交换机即可，消费者从该交换机上拿消息
        channel.queueBind(ConnectionUtil.STREAM_QUEUE_NAME1, ConnectionUtil.STREAM_EXCHANGE,
                ConnectionUtil.STREAM_ROUTING_KEY1);

        String message = "Hello World!333";
        channel.basicPublish(ConnectionUtil.STREAM_EXCHANGE, ConnectionUtil.STREAM_ROUTING_KEY1, null,
                message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
