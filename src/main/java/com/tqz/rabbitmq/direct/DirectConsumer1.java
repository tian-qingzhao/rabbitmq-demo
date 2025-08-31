package com.tqz.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

/**
 * @Author: tian
 * @Date: 2020/4/25 22:18
 * @Desc: 消费者 测试direct交换机
 */
public class DirectConsumer1 {

    public static void main(String[] args) throws Exception {
        getMessage();
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        // 创建一个通道，一个连接可以创建多个通道
        Channel channel = connection.createChannel();

        // 客户端拉模式
//        GetResponse getResponse = channel.basicGet(ConnectionUtil.QUEUE_NAME1, false);
//        byte[] body = getResponse.getBody();
//        int messageCount = getResponse.getMessageCount();
//        AMQP.BasicProperties props = getResponse.getProps();
//        Envelope envelope = getResponse.getEnvelope();


        //设置消费者每次从队列中获取指定的条数, 此时如果没有应答的话，消费者将不再继续获取
//        channel.basicQos(12);
        channel.queueDeclare(ConnectionUtil.DIRECT_QUEUE_NAME1, true, false, false, null);
        //服务端推模式，接受消息，开始消费
        channel.basicConsume(ConnectionUtil.DIRECT_QUEUE_NAME1, new MyDefaultConsumer(channel));
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
