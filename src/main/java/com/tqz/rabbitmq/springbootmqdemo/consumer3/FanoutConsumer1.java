package com.tqz.rabbitmq.springbootmqdemo.consumer3;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;

/**
 * @Author: tian
 * @Date: 2020/4/27 1:54
 * @Desc: 测试消息预取
 */
public class FanoutConsumer1 {

    public static void main(String[] args) throws Exception {
        getMessage();
    }
    static int i;

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(ConnectionUtil.QUEUE_NAME1,true,false,false,null);
        long time = new Date().getTime();

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //consumerTag 消费者的标识
                //envelope 包含消息的交换机，消息的路由键，消息的标识
                //properties 消息的配置
                //body 消息体

                //手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识
                //第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
                i++;
//                System.out.println(new String(body,"UTF-8")+i);

                if (i==20000 || i%2500==0){
                    channel.basicAck(envelope.getDeliveryTag(), true);
                    System.out.println("耗时：" + (new Date().getTime()-time));
                }
            }
        };
        //预取的数量
        channel.basicQos(2500);
        //接受消息，开始消费
        channel.basicConsume(ConnectionUtil.QUEUE_NAME1,defaultConsumer);
        //关闭资源，这里不关闭资源的原因是因为如果再有消息进来就接受不到了。
//        channel.close();
//        connection.close();
    }
}
