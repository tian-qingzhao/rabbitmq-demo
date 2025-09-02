package com.tqz.rabbitmq.direct;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.tqz.rabbitmq.ConnectionUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: tian
 * @Date: 2020/4/25 22:18
 * @Desc: 直连交换机生产者  测试direct交换机
 * 路由键完全匹配才会发送到相应的队列,
 * 如果不指定路由键，默认会使用队列的名称作为路由键。
 * 三种交换机使用的默认类型也是direct
 */
public class DirectProducer {

    public static void main(String[] args) throws Exception {
        //获取连接
        Connection connection = ConnectionUtil.getConnection();
        //创建一个通道，一个连接可以创建多个通道
        Channel channel = connection.createChannel();
        // 如果一个连接中存在多个channel，可以通过指定参数来标识第几个channel
        // 如果存在重复指定，则返回null
//        connection.createChannel(0);

        Map<String, Object> params = new HashMap<>();
        // 经典队列是Rabbit MQ默认的队列，这里默认都是classic类型的。与Spring Boot整合时，使用new Queue，创建的队列也是普通队列
//        params.put("x-queue-type", "classic");
        // Quorum是针对镜像队列的一种优化，目前已经取代了镜像队列，作为Rabbit MQ集群部署保证高可用性的解决方案。
        // 传统的镜像队列，是将消息副本存储在一组节点上，以提高可用性和可靠性。镜像队列将队列中的消息复制到一个或多个其他节点上，并使这些节点上的队列保持同步。
        // 当一个节点失败时，其他节点上的队列不受影响，因为它们上面都有消息的备份。
        // 镜像队列使用主从模式，所有消息写入和读取均通过主节点，并异步复制到镜像节点。主节点故障时需重新选举，期间队列不可用。而仲裁队列基于Raft
        // 分布式共识算法，所有节点组成仲裁组。消息需被多数节点持久化后才确认成功，Leader故障时自动触发选举。
        // 相比较于传统的主从模式，避免了发生网络分区时的脑裂问题（基于Raft分布式共识算法避免）。
        // 相比较于普通队列，仲裁队列增加了一个对于有毒消息的处理。什么是有毒消息？首先，消费者从队列中获取到了元素，队列会将该元素删除，
        // 但是消费者消费失败了，会给队列nack，并且可以设置消息重新入队。这样可能存在因为业务代码的问题，某条消息一直处理不成功的问题。
        // 仲裁队列会记录消息的重新投递次数，判断是否超过了设置的阈值，如果超过了就直接丢弃，或者放入死信队列人工处理。
        // 仲裁队列适用于集群环境下，队列长期存在，并且对于消息可靠性要求高，允许牺牲一部分性能（因为raft算法，消息需被多数节点持久化后才确认成功）的场景。
//        params.put("x-queue-type", "quorum");
        // 在传统的队列模型中，同一条消息只能被一个消费者消费（一个队列如果有多个消费者，是工作分发的机制。消息1->消费者1，消息2->消费者2，消息3->消费者1，
        // 不能两个消费者读同一条消息。），并且消息是阅后即焚的（消费者接收到消息后，队列中的该消息就删除，
        // 如果消费者拒绝签收并且设置了重新入队，再把消息重新放入队列中），无法重复从队列中获取相同的消息。并且在当队列中积累的消息过多时，性能下降会非常明显。
        // Stream队列正是解决了以上的这些问题。Stream队列的核心是用aof文件的形式存储队列，将消息以aof的方式追加到文件中。
        // 允许用户在日志的任何一个连接点开始重新读取数据。（需要用户自己记录偏移量）
//        params.put("x-queue-type","stream");
        // Rabbit MQ对于常规队列的处理是，将消息优先存在于内存中，在合适的时机再持久化到磁盘上，
        // 而懒队列则相反，懒队列会尽可能早的将消息内容保存到磁盘当中，并且只有在用户请求到时，才临时从磁盘加载到内存当中。懒队列的设计也是为了应对消息堆积问题的。
        //声明懒队列的方式，只需要加入参数，相应的，当一个队列被声明为懒队列，那即使队列被设定为不持久化，消息依然会写入到硬盘中。
        // 懒队列适合消息量大且长期有堆积的队列，可以减少内存使用，加快消费速度。
//        params.put("x-queue-mode", "lazy");

        // 存活时间。10秒钟不使用超时。创建queue时参数arguments设置了x-expires参数，
        // 该queue会在x-expires到期后未被使用的情况下删除，
        // 未被使用包括以下三点：1.没有任何消费者 2.未被重新声明过期时间 3.未调用过Basic.Get命令
        // 亲身测试直接消失（哪怕里面有未消费的消息）。
//        params.put("x-expires", 10000);
        // 消息存活时间。5分钟过期。创建queue时设置该参数可指定消息在该queue中待多久，
        // 可根据x-dead-letter-routing-key和x-dead-letter-exchange生成可延迟的死信队列。
//        params.put("x-message-ttl", 300000);
        // 死信交换机。创建queue时参数arguments设置了x-dead-letter-routing-key和x-dead-letter-exchange，
        // 会在x-message-ttl时间到期后把消息放到x-dead-letter-routing-key和x-dead-letter-exchange指定的队列中达到延迟队列的目的。
//        params.put("x-dead-letter-exchange", "dlx");
        // 死信路由键。这里的routing-key也可以是队列名称，当消息过期后会转发到这个exchange对应的routing-key，达到延时队列效
//        params.put("x-dead-letter-routing-key", "dead.orders");
        // 创建优先级队列，支持10级优先级。创建queue时arguments可以使用x-max-priority参数声明优先级队列
        // 建议使用1到10之间。目前使用更多的优先级将消耗更多的资源（Erlang进程）。
        // 设置该参数同时设置死信队列时或造成已过期的低优先级消息会在未过期的高优先级消息后面执行。
        // 该参数会造成额外的CPU消耗。
//        params.put("x-max-priority", 10);
        // 限制队列长度，最多1000条消息。限制加入queue中消息的条数。先进先出原则，超过10条后面的消息会顶替前面的消息。
//        params.put("x-max-length", 1000);
        // 消息容量限制,该参数是非负整数值。stream类型的队列所需要，该参数和x-max-length目的一样限制队列的容量，但是这个是靠队列大小（bytes）来达到限制。
//        params.put("x-max-length-bytes", 1024);
        // size of segment files: 100 MB
        params.put("x-stream-max-segment-size-bytes", 100_000_000);


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
        // RabbitMQ中已存在这个队列，但在启动的项目中对这个队列的属性进行了修改。RabbitMQ中的队列一经声明，其属性不可修改。

        channel.queueDeclare(ConnectionUtil.DIRECT_QUEUE_NAME1, true, false, false, params);

        // 第一个参数：交换机的名称
        // RabbitMQ中已存在这个交换机，但在启动的项目中对这个交换机的属性进行了修改。RabbitMQ中的队列一经声明，其属性不可修改。
        // ExchangeTypes.DIRECT
        channel.exchangeDeclare(ConnectionUtil.DIRECT_EXCHANGE, "direct", true, false, null);

        // 删除交换机名称
//        channel.exchangeDelete("test");

        // 把交换机给队列绑定上
        // 生产者如果不想声明队列和队列绑定到交换机的步骤，也可以在消费者去实现，
        // 生产者只需要定义交换机即可，消费者从该交换机上拿消息
        // 处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。
        // 这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “test”，
        // 则只有被标记为“test”的消息才被转发，不会转发test.aaa，也不会转发dog.123，只会转发test。
        channel.queueBind(ConnectionUtil.DIRECT_QUEUE_NAME1, ConnectionUtil.DIRECT_EXCHANGE, "info.user");
        channel.queueBind(ConnectionUtil.DIRECT_QUEUE_NAME2, ConnectionUtil.DIRECT_EXCHANGE, "error.user");
        channel.queueBind(ConnectionUtil.DIRECT_QUEUE_NAME3, ConnectionUtil.DIRECT_EXCHANGE, "debug.user");
        channel.queueBind(ConnectionUtil.DIRECT_QUEUE_NAME4, ConnectionUtil.DIRECT_EXCHANGE, "info.user");

        for (int i = 0; i < 10; i++) {
            String messageId = UUID.randomUUID().toString();

            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .contentType("application/json") // 内容类型
                    .priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode()) // 优先级（0-9）
                    .deliveryMode(MessageProperties.PERSISTENT_TEXT_PLAIN.getDeliveryMode()) // 2=持久化（存硬盘）
                    .expiration("60000") // 消息60秒过期
                    .messageId(messageId)
                    .build();
            System.out.println("messageId：" + messageId);

            String message = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
            // 第一个参数 exchange：交换机的名称，不写的话会用默认的 (AMQP default) 名称， direct类型的
            // 第二个参数 routingKey：为路由键，如果交换机用默认的，路由键会使用队列的名称
            // 第三个参数 props：为 消息的基本属性，例如路由头等
            // 第四个参数 body：为消息体
            channel.basicPublish(ConnectionUtil.DIRECT_EXCHANGE, "info.user", props, message.getBytes());

            //关闭资源
            // channel.close();
            // connection.close();

            Thread.sleep(800);
        }
    }
}
