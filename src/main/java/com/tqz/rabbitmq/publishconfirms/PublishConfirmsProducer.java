package com.tqz.rabbitmq.publishconfirms;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ReturnListener;
import com.tqz.rabbitmq.ConnectionUtil;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @Author: tian
 * @Date: 2020/4/24 22:27
 * @Desc: 生产者
 */
public class PublishConfirmsProducer {

    static final int MESSAGE_COUNT = 50_000;

    public static void main(String[] args) throws Exception {
        publishConfirmsMessagesIndividually();
//        publishConfirmsMessagesInBatch();
//        publishConfirmsAsynchronously();
    }

    static void publishConfirmsMessagesIndividually() throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建一个通道
        Channel channel = connection.createChannel();
        // 开启确认模式
        channel.confirmSelect();

        channel.queueDeclare(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1, true, false, false, null);
        channel.exchangeDeclare(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, "direct");
        channel.queueBind(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1,
                ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1);

        channel.confirmSelect();
        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String body = String.valueOf(i);
            channel.basicPublish(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE,
                    ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1, null, body.getBytes());
            channel.waitForConfirmsOrDie(5_000);
        }
        long end = System.nanoTime();
        System.out.format("Published %,d messages individually in %,d ms%n", MESSAGE_COUNT,
                Duration.ofNanos(end - start).toMillis());
    }

    static void publishConfirmsMessagesInBatch() throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建一个通道
        Channel channel = connection.createChannel();
        // 开启确认模式
        channel.confirmSelect();

        channel.queueDeclare(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1, true, false, false, null);
        channel.exchangeDeclare(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, "direct");
        channel.queueBind(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1,
                ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1);

        channel.confirmSelect();

        int batchSize = 100;
        int outstandingMessageCount = 0;

        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String body = String.valueOf(i);
            channel.basicPublish(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE,
                    ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1, null, body.getBytes());
            outstandingMessageCount++;

            if (outstandingMessageCount == batchSize) {
                channel.waitForConfirmsOrDie(5_000);
                outstandingMessageCount = 0;
            }
        }

        if (outstandingMessageCount > 0) {
            channel.waitForConfirmsOrDie(5_000);
        }
        long end = System.nanoTime();
        System.out.format("Published %,d messages in batch in %,d ms%n", MESSAGE_COUNT,
                Duration.ofNanos(end - start).toMillis());
    }

    static void publishConfirmsAsynchronously() throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建一个通道
        Channel channel = connection.createChannel();
        // 开启确认模式
        channel.confirmSelect();

        channel.queueDeclare(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1, true, false, false, null);
        channel.exchangeDeclare(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, "direct");
        channel.queueBind(ConnectionUtil.PUBLISH_CONFIRMS_QUEUE_NAME1,
                ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE, ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1);

        ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        // 添加确认机制。对应spring的ConfirmCallback
        // 消息的确认，是指生产者投递消息后，如果broker收到消息，则会给生产者一个应答
        // 生产者进行接收应答，用来确定这条消息是否正常的发送到broker，这种方式也是消息的可靠性投递的核心保障
        channel.addConfirmListener(new ConfirmListener() {
            // 返回成功的回调函数
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                System.out.println("ConfirmListener#handleAck 回调函数接收到参数 deliveryTag："
                        + deliveryTag + ",multiple：" + multiple);

                if (multiple) {
                    ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(
                            deliveryTag, true
                    );
                    confirmed.clear();
                } else {
                    outstandingConfirms.remove(deliveryTag);
                }
            }

            // 返回失败的回调函数
            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                System.out.println("ConfirmListener#handleNack 回调函数接收到参数 deliveryTag："
                        + deliveryTag + ",multiple：" + multiple +
                        "，失败消息内容：" + outstandingConfirms.get(deliveryTag));
            }
        });

        // 当发送消息时交互机不存在、或者队列不存在，或者Routing-key错误，那么相应的return listener就可以工作。
        // 对应spring的ReturnCallback。
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey,
                                     AMQP.BasicProperties properties, byte[] body) {
                System.out.println("ReturnListener#handleReturn 回调函数接收到参数 replyCode：" + replyCode +
                        ",replyText：" + replyText + ",exchange：" + exchange + ",routingKey：" + routingKey +
                        ",properties：" + properties + ",body：" + new String(body));
            }
        });

        //用于记录每发布100条消息进行一次确认
        int batchSize = 10;
        int outstandingMessageCount = 0;
        long start = System.nanoTime();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
            // 交换机要先自己创建，server端没有交换机的话会报错。reply-code=404, reply-text=NOT_FOUND - no exchange '2020-04-25 20:32
            // exchange' in vhost '/'
            // 或者使用上面的 channel.exchangeDeclare(EXCHANGE_NAME, "direct"); 声明交换机
            // 路由键不存在也会报错： replyCode：312,replyText：NO_ROUTE，并且会回调ReturnListener函数
            // 参数mandatory 设置为true，当发送消息不可达时，会执行ReturnListener，如果为false则会删除该消息。默认false

            // getNextPublishSeqNo()已经处理好了线程安全问题
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);

            //第一个参数 exchange：交换机的名称，不写的话会用默认的 (AMQP default) 名称， direct类型的
            //第二个参数 routingKey：为路由键，如果交换机用默认的，路由键会使用队列的名称
            //第三个参数 props：为 消息的基本属性，例如路由头等
            //第四个参数 body：为消息体
            channel.basicPublish(ConnectionUtil.PUBLISH_CONFIRMS_EXCHANGE,
                    ConnectionUtil.PUBLISH_CONFIRMS_ROUTING_KEY1,
                    true, null, message.getBytes());

            outstandingMessageCount++;
            // 每100次确认一次
            if (outstandingMessageCount == batchSize) {
                // uses a 5 second timeout
                // 如果超时过期，则抛出TimeoutException。如果任何消息被nack(丢失）, waitForConfirmsOrDie将抛出IOException。
                channel.waitForConfirmsOrDie(5_000);
                outstandingMessageCount = 0;
            }

            //关闭资源
//            channel.close();
//            connection.close();
//            Thread.sleep(1000);
        }

        long end = System.nanoTime();
        System.out.format("Published %,d messages and handled confirms asynchronously in %,d ms%n", MESSAGE_COUNT
                , Duration.ofNanos(end - start).toMillis());
    }
}
