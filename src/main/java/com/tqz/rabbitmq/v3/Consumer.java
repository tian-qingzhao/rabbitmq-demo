package com.tqz.rabbitmq.v3;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author: tian
 * @Date: 2020/4/24 22:59
 * @Desc: 消费者
 */
public class Consumer {

    private static final String QUEUE_NAME = "2020-04-25 20:32 queue";

    public static void main(String[] args) throws Exception {
        getMessage();
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        //设置消费者每次从队列中获取指定的条数, 此时如果没有应答的话，消费者将不再继续获取
//        channel.basicQos(12);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                long deliveryTag = envelope.getDeliveryTag();

                //consumerTag 消费者的标识
                //envelope 包含消息的交换机，消息的路由键，消息的标识
                //properties 消息的配置
                //body 消息体
                System.out.println("consumerTag：" + consumerTag + ",deliveryTag：" + deliveryTag + ",body：" +
                        new String(body, StandardCharsets.UTF_8));
                //手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识。
                // 每次向信道进行投递的时候都会产生一个投递标签号，每次加1，作为唯一标识。
                //第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
                channel.basicAck(deliveryTag, false);

                // deliveryTag表示被拒绝的消息的投递标签；
                // multiple表示是否批量拒绝，若是则所有投递标签小于当前消息且未确认的消息也都将被拒绝，若否则仅拒绝当前消息；
                // requeue表示被拒绝的消息是否重新放回队列，若是则消息会重新回到队列并选择新的消费者进行投递，若否则该条消息会被丢弃。
                // requeue设置为true表示重新返回到队列，false表示丢弃。
                //channel.basicNack(envelope.getDeliveryTag(), false, false);
            }
        };

        // 接受消息，开始消费
        channel.basicConsume(QUEUE_NAME, false, defaultConsumer);
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
