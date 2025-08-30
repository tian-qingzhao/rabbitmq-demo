/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:11:04
 */
package com.tqz.rabbitmq.v2.service;

import java.util.List;

import com.tqz.rabbitmq.v2.pojo.Userinfo;

/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:11:04
 */
public interface UserinfoService {

	//添加
	public void insert(Userinfo userinfo);
	
	//查询全部
	List<Userinfo> getList();
}
