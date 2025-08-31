package com.tqz.rabbitmq.workqueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.tqz.rabbitmq.ConnectionUtil;

/**
 * 没有交换机的生产者.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2025/8/31 14:25
 */
public class Producer {

    /**
     * 发布一个task，交由多个Worker去处理。 每个task只要由一个Worker完成就行。
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(ConnectionUtil.WORK_QUEUE_NAME, true, false, false, null);

        for (int i = 0; i < 5; i++) {
            String message = "task " + i;
            channel.basicPublish("", ConnectionUtil.WORK_QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
