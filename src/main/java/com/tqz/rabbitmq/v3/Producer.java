package com.tqz.rabbitmq.v3;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: tian
 * @Date: 2020/4/24 22:27
 * @Desc: 生产者
 */
public class Producer {

    private static final String EXCHANGE_NAME = "2020-04-25 20:32 exchange";

    private static final String QUEUE_NAME = "2020-04-25 20:32 queue";

    private static final String ROUTING_KEY = "2020-04-25 20:32 routingKey";

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();
        //创建一个通道
        Channel channel = connection.createChannel();

        //第一个参数 queue：队列的名字，
        //第二个参数 durable：队列是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，
        //如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
        //第三个参数 exclusive：表示队列是否单一,exclusive：是否排外的，有两个作用，
        //        一：当连接关闭时connection.close()该队列是否会自动删除；
        //        二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，
        //        没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如果强制访问会报异常：
        //第四个参数 autoDelete：是否自动删除队列，当最后一个消费者断开连接之后队列是否自动被删除，
        //可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
        //第五个参数 arguments：相关参数，如最大能容纳多少条消息以及消息的寿命等，目前一般为null
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        //第一个参数 exchange：交换机的名称，不写的话会用默认的 (AMQP default) 名称， direct类型的
        //第二个参数 routingKey：为路由键，如果交换机用默认的，路由键会使用队列的名称
        //第三个参数 props：为 消息的基本属性，例如路由头等
        //第四个参数 body：为消息体
        String message = "2020-04-24 22:52分，练习RabbitMQ";
        AtomicInteger value = new AtomicInteger();
        while (true) {
            String tempMessage = message;
            tempMessage = tempMessage + value.incrementAndGet();
            // 交换机要先自己创建，server端没有交换机的话会报错。reply-code=404, reply-text=NOT_FOUND - no exchange '2020-04-25 20:32 exchange' in vhost '/'
            // 或者使用上面的 channel.exchangeDeclare(EXCHANGE_NAME, "direct"); 声明交换机
            // 路由键不存在也会报错： replyCode：312,replyText：NO_ROUTE，并且会回调ReturnListener函数
            // 参数mandatory 设置为true，当发送消息不可达时，会执行ReturnListener，如果为false则会删除该消息。默认false
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, null, tempMessage.getBytes());
            //关闭资源
//            channel.close();
//            connection.close();
            Thread.sleep(1000);

            // 添加确认机制。对应spring的ConfirmCallback
            // 消息的确认，是指生产者投递消息后，如果broker收到消息，则会给生产者一个应答
            // 生产者进行接收应答，用来确定这条消息是否正常的发送到broker，这种方式也是消息的可靠性投递的核心保障
            channel.addConfirmListener(new ConfirmListener() {
                // 返回成功的回调函数
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("ConfirmListener#handleAck 回调函数接收到参数 deliveryTag："
                            + deliveryTag + ",multiple" + multiple);
                }

                // 返回失败的回调函数
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("ConfirmListener#handleNack 回调函数接收到参数 deliveryTag："
                            + deliveryTag + ",multiple" + multiple);
                }
            });

            // 当发送消息时交互机不存在、或者队列不存在，或者Routing-key错误，那么相应的return listener就可以工作。
            // 对应spring的ReturnCallback。
            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
                                         AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("ReturnListener#handleReturn 回调函数接收到参数 replyCode：" + replyCode +
                            ",replyText：" + replyText + ",exchange：" + exchange + ",routingKey：" + routingKey +
                            ",properties：" + properties + ",body：" + new String(body));
                }
            });

        }
    }
}
