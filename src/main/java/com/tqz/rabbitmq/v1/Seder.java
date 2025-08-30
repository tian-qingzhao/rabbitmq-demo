package com.tqz.rabbitmq.v1;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送者
 * @author 田
 * @data 2019年10月29日 下午10:30:16
 */
@Component
public class Seder {

	@Autowired
	private AmqpTemplate amqpTemplate;

	/**
	 * 创建队列
	 * authro 田
	 * date 2019年10月29日 下午10:31:46
	 * @param msg
	 */
	public void send(String msg){
		//向消息队列发送消息:参数一表示队列的名称，参数二表示要发送的消息内容
		amqpTemplate.convertAndSend("hello-queue",msg);
	} 
	
}
