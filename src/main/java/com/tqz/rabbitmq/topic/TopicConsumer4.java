package com.tqz.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

/**
 * @Author: tian
 * @Date: 2020/4/25 22:18
 * @Desc: 消费者 测试topic交换机
 */
public class TopicConsumer4 {

    public static void main(String[] args) throws Exception {
        getMessage();
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //设置消费者每次从队列中获取指定的条数, 此时如果没有应答的话，消费者将不再继续获取
//        channel.basicQos(12);
        channel.queueDeclare(ConnectionUtil.TOPIC_QUEUE_NAME4, true, false, false, null);
        //接受消息，开始消费
        channel.basicConsume(ConnectionUtil.TOPIC_QUEUE_NAME4, new MyDefaultConsumer(channel));
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
