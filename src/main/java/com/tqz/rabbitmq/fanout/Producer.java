package com.tqz.rabbitmq.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.tqz.rabbitmq.fanout.ConnectionUtil;

/**
 * @Author: tian
 * @Date: 2020/4/25 21:13
 * @Desc: 生产者，测试fanout交换机
 *        可以不使用路由键，只需要绑定队列就可以
 */
public class Producer {

    public static void main(String[] args) throws Exception{
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
        channel.queueDeclare(ConnectionUtil.QUEUE_NAME1,true,false,false,null);

        //第一个参数：交换机的名称，第二个参数：交换机的类型
        channel.exchangeDeclare(ConnectionUtil.FANOUT_EXCHANGE,"fanout");

        //把交换机给队列绑定上
        channel.queueBind(ConnectionUtil.QUEUE_NAME1,ConnectionUtil.FANOUT_EXCHANGE,"");
        channel.queueBind(ConnectionUtil.QUEUE_NAME3,ConnectionUtil.FANOUT_EXCHANGE,"");
        //第一个参数 exchange：交换机的名称，不写的话会用默认的 (AMQP default) 名称， direct类型的
        //第二个参数 routingKey：为路由键，如果交换机用默认的，路由键会使用队列的名称
        //第三个参数 props：为 消息的基本属性，例如路由头等
        //第四个参数 body：为消息体
        String message = "2020-04-24 22:52分，练习RabbitMQ";
        while (true){
            channel.basicPublish(ConnectionUtil.FANOUT_EXCHANGE,"",null,message.getBytes());
            //关闭资源
//            channel.close();
//            connection.close();
            Thread.sleep(800);
        }
    }
}
