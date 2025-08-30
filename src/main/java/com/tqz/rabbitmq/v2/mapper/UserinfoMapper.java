/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:14:39
 */
package com.tqz.rabbitmq.v2.mapper;

import java.util.List;

import com.tqz.rabbitmq.v2.pojo.Userinfo;
import org.springframework.stereotype.Repository;

/**
 * @Author: tian
 * @Date: 2019年10月30日下午2:14:39
 */
@Repository
public interface UserinfoMapper {

	public void insert(Userinfo userinfo);
	
	List<Userinfo> getList();
}
