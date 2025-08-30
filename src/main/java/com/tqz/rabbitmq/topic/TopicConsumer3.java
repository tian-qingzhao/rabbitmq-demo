package com.tqz.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @Author: tian
 * @Date: 2020/4/25 22:18
 * @Desc: 消费者 测试topic交换机
 */
public class TopicConsumer3 {

    public static void main(String[] args) throws Exception {
        getMessage();
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //设置消费者每次从队列中获取指定的条数, 此时如果没有应答的话，消费者将不再继续获取
//        channel.basicQos(12);
        channel.queueDeclare(ConnectionUtil.QUEUE_NAME3,true,false,false,null);
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //consumerTag 消费者的标识
                //envelope 包含消息的交换机，消息的路由键，消息的标识
                //properties 消息的配置
                //body 消息体
                System.out.println(new String(body,"UTF-8"));
                //手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识
                //第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //接受消息，开始消费
        channel.basicConsume(ConnectionUtil.QUEUE_NAME3,defaultConsumer);
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
