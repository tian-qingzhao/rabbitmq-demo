/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:11:46
 */
package com.tqz.rabbitmq.v2.service;

import java.util.List;

import com.tqz.rabbitmq.v2.mapper.UserinfoMapper;
import com.tqz.rabbitmq.v2.pojo.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:11:46
 */
@Service
public class UserinfoServiceImpl implements UserinfoService{

	@Autowired
	private UserinfoMapper userinfoMapper;
	/**
	 * 添加用户
	 */
	@Override
	public void insert(Userinfo userinfo) {
		userinfoMapper.insert(userinfo);
	}
	
	/**
	 * 查询所有
	 */
	@Override
	public List<Userinfo> getList() {
		return userinfoMapper.getList();
	}
	
	
}
