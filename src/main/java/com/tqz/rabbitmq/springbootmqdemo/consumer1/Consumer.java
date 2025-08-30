package com.tqz.rabbitmq.springbootmqdemo.consumer1;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author: tian
 * @Date: 2020/4/26 0:24
 * @Desc:
 */
@Component
public class Consumer {

    /**
     * 监听队列名为 topicQueue1
     * @param message
     */
    @RabbitListener(queues = "0426 15:46 Queue",containerFactory = "simpleRabbitListenerContainerFactory")
    public void get2(Message message, Channel channel)throws Exception{
        String msg = new String(message.getBody());
        // 下订单成功才进行确认消息，要不然就让消息重回队列
        if (orderIsSuccess()){
            // 手动消息确认 第一个参数 deliveryTag:该消息的index,唯一标识
            // 第二个参数 multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }else {
            //批量退回，第一个参数消息的唯一标识，第二个参数是否批量退回，第三个是参数是否重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
            //单条退回
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

      /*  JSONObject jsonObject = JSON.parseObject(msg);
        List<User> userList = new ArrayList<>();
        if (jsonObject != null){
            String userName = jsonObject.getString("userName");
            String passWord = jsonObject.getString("passWord");
            User user = new User(userName,passWord);
            userList.add(user);
        }*/
        System.out.println("消费者1拿到的消息：" + new String(message.getBody(),"UTF-8"));
    }

    /**
     * 订单是否下成功，模拟真实下订单业务（下订单成功返回true，失败返回false）
     * @return
     */
    public boolean orderIsSuccess(){
        return true;
    }
}
