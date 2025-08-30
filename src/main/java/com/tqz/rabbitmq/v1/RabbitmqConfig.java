package com.tqz.rabbitmq.v1;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建消息队列
 * @author 田
 * @data 2019年10月29日 下午10:28:35
 */
@Configuration
public class RabbitmqConfig {

	/**
	 * 创建队列
	 * authro 田
	 * date 2019年10月29日 下午10:29:42
	 * @return
	 */
	@Bean
	public Queue createQueue(){
		return new Queue("hello-queue");
	}
}
