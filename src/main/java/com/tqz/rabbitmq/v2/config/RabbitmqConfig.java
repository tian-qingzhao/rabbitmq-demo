/**
 * @Author: tian
 * @Date: 2019年10月30日下午1:42:43
 */
package com.tqz.rabbitmq.v2.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @Author: tian
 * @Date: 2019年10月30日下午1:42:43
 */
@Configuration
public class RabbitmqConfig {

	public static final String QUEUE_NAME = "spring-boot-rabbitmq";
	public static final String EXCHANGE_NAME = "spring-boot-exchange";
	public static final String ROUTING_KEY = "spring-boot-routing-key";
	
	/**
	 * 创建队列
	 * @Author: tian
	 * @Date: 2019年10月30日 下午1:49:58
	 * @Desc:
	 */
	@Bean
	public Queue queue(){
		return new Queue(QUEUE_NAME);
	}
	
	/**
	 * 创建一个topic类型的交换器
	 * @Author: tian
	 * @Date: 2019年10月30日 下午1:51:06
	 * @Desc:
	 */
	@Bean
	public TopicExchange exchange(){
		return new TopicExchange(EXCHANGE_NAME);
	}
	
	/**
	 * 使用路由键(routingKey)把队列(Queue)绑定到交换机(exchange)
	 * @Author: tian
	 * @Date: 2019年10月30日 下午1:55:07
	 * @Desc:
	 */
	@Bean
	public Binding bingDing(Queue queue,TopicExchange topicExchange){
		return BindingBuilder.bind(queue).to(topicExchange).with(ROUTING_KEY);
	}
	
	/**
	 * 配置连接
	 * @Author: tian
	 * @Date: 2019年10月30日 下午2:05:46
	 * @Desc:
	 */
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("106.13.39.231",5672);
		cachingConnectionFactory.setUsername("tian");
		cachingConnectionFactory.setPassword("123456");
		return cachingConnectionFactory;
	}
	
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
		return new RabbitTemplate(connectionFactory);
	}
}
