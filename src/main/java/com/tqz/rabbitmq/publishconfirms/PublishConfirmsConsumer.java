package com.tqz.rabbitmq.publishconfirms;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.ConnectionUtil;
import com.tqz.rabbitmq.MyDefaultConsumer;

/**
 * @Author: tian
 * @Date: 2020/4/24 22:59
 * @Desc: 消费者
 */
public class PublishConfirmsConsumer {

    public static void main(String[] args) throws Exception {
        getMessage();
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //设置消费者每次从队列中获取指定的条数, 此时如果没有应答的话，消费者将不再继续获取
//        channel.basicQos(12);

        // 接受消息，开始消费
        channel.basicConsume(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1, false, new MyDefaultConsumer(channel));
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
