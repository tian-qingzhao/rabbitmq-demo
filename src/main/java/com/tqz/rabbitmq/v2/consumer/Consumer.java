/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:42:05
 */
package com.tqz.rabbitmq.v2.consumer;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.tqz.rabbitmq.v2.config.RabbitmqConfig;
import com.tqz.rabbitmq.v2.pojo.Userinfo;
import com.tqz.rabbitmq.v2.service.UserinfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Console;

/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:42:05
 */
@Component
@Service
public class Consumer {

	@Autowired
	private UserinfoService userinfoService;
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private static final String USER_KEY = "USER";
	
	//此消息队列 RabbitmqConfig.QUEUE_NAME 一旦有了消息进入队列就被监听被处理
    @RabbitListener(queues = RabbitmqConfig.QUEUE_NAME)
	public void consumerMessage(String message){
		if(StringUtils.isNotBlank(message)){
			Userinfo user = new Userinfo();
			user.setUserName("tian");
			user.setUserPass("111111");
			user.setUserRole("技术总监");
			//存到数据库中
			userinfoService.insert(user);
			//从redis中通过键得到value
			String userKey = stringRedisTemplate.opsForValue().get(USER_KEY);
			//如果redis中有，就删除当前key
			if(StringUtils.isNotBlank(userKey)){
				stringRedisTemplate.delete(USER_KEY);
			}
		}
		Console.log("消费了："+message);
	}

    public List<Userinfo> getList(){
    	List<Userinfo> list = userinfoService.getList();
    	//转换为json格式
    	String jsonObject = JSONObject.toJSONString(list);
    	//往redis中存储
    	stringRedisTemplate.opsForValue().set(USER_KEY, jsonObject);
    	return list;
    }
}
