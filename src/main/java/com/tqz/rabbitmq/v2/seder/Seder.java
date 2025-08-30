/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:09:26
 */
package com.tqz.rabbitmq.v2.seder;


import cn.hutool.core.date.DateTime;
import com.tqz.rabbitmq.v2.config.RabbitmqConfig;
import com.tqz.rabbitmq.v2.consumer.Consumer;
import com.tqz.rabbitmq.v2.pojo.Userinfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:09:26
 */
@RestController
@RequestMapping("/web/rabbitmq")
@Slf4j
public class Seder {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private Consumer consumer;
	
	/**
	 * 发送消息方法
	 * @Author: tian
	 * @Date: 2019年10月30日 下午2:24:58
	 * @Desc:
	 */
	@GetMapping("seder")
	public String sederMessage(){
		List<Thread> list = new ArrayList();
		for(int i=0;i<10;i++){
			Thread thread = new Thread(()->{
			String dateTime = new DateTime().toString("YYYY-MM-dd HH:mm:ss");
            log.info("seder message:" + dateTime);
			rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME,RabbitmqConfig.ROUTING_KEY, dateTime);
			});
			list.add(thread);
		}
		list.forEach(Thread:: start);
		return "OK!";
	}
	
	@GetMapping("/getList")
	public List<Userinfo> getList(){
		List<Userinfo> list = consumer.getList();
		return list;
	}
}
