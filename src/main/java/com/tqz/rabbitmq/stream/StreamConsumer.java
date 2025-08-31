package com.tqz.rabbitmq.stream;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * stream类型的队列消费者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 17:39
 */
public class StreamConsumer {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 1、这个属性必须设置。
        channel.basicQos(1);
        // 2、声明Stream队列
        Map<String, Object> params = new HashMap<>();
        params.put("x-queue-type", "stream");
        // maximum stream size: 20 GB
        params.put("x-max-length-bytes", 20_000_000_000L);
        // size of segment files: 100 MB
        params.put("x-stream-max-segment-size-bytes", 100_000_000);
        channel.queueDeclare(ConnectionUtil.STREAM_QUEUE_NAME1, true, false, false, params);

        // 3、消费时，必须指定offset。 可选的值：
        // first: 从日志队列中第一个可消费的消息开始消费
        // last: 消费消息日志中最后一个消息
        // next: 相当于不指定offset，消费不到消息。
        // Offset: 一个数字型的偏移量
        // Timestamp:一个代表时间的Data类型变量，表示从这个时间点开始消费。
        // 例如 一个小时前 Date timestamp = new Date(System.currentTimeMillis() - 60 * 60 * 1_000)
        Map<String, Object> consumeParam = new HashMap<>();
        consumeParam.put("x-stream-offset", "last");
        channel.basicConsume(ConnectionUtil.STREAM_QUEUE_NAME1, false, consumeParam, new MyDefaultConsumer(channel));

//        channel.close();
    }
}
