package com.foodie.web.service;

import com.foodie.core.generic.GenericService;
import com.foodie.web.model.User;

/**
 * 用户 业务 接口
 * 
 * @author StarZou
 * @since 2014年7月5日 上午11:53:33
 **/
public interface IUserService{

	User selectByUsername(String username);

	void insert(User user);

	User selectByPhone(String phone);

	User selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(User user);
}
