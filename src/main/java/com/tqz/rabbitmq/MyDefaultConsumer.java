package com.tqz.rabbitmq;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 默认消费者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 12:34
 */
public class MyDefaultConsumer extends DefaultConsumer {

    public MyDefaultConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                               byte[] body) throws IOException {
        long deliveryTag = envelope.getDeliveryTag();

        try {
            // consumerTag 消费者的标识
            // envelope 包含消息的交换机，消息的路由键，消息的标识
            // properties 消息的配置
            // body 消息体
            // messageId 业务方自定义的消息的id，
            System.out.println(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_FORMAT) +
                    "，consumerTag：" + consumerTag + ",deliveryTag" + "：" + deliveryTag +
                    "，contentType：" + properties.getContentType() +
                    "，" + "body：" + new String(body, StandardCharsets.UTF_8) +
                    "，messageId：" + properties.getMessageId());
            //手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识。
            // 每次向信道进行投递的时候都会产生一个投递标签号，每次加1，作为唯一标识。
            //第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            getChannel().basicAck(deliveryTag, false);
        } catch (Exception e) {
            // deliveryTag表示被拒绝的消息的投递标签；
            // multiple表示是否批量拒绝，若是则所有投递标签小于当前消息且未确认的消息也都将被拒绝，若否则仅拒绝当前消息；
            // requeue表示被拒绝的消息是否重新放回队列，若是则消息会重新回到队列并选择新的消费者进行投递，若否则该条消息会被丢弃。
            // requeue设置为true表示重新返回到队列，false表示丢弃。
            getChannel().basicNack(envelope.getDeliveryTag(), false, true);

            // 拒绝消息，多个消息会批量拒绝
            // getChannel().basicReject(deliveryTag, true);
        }
    }
}
