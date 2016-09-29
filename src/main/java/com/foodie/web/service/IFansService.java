package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Fans;

public interface IFansService {
	int insert(Fans fans);

	Fans selectByTwoId(String userId,String fansId);

	int delete(Fans fans);

	List<Fans> selectByUserId(String userId);
	
	List<Fans> selectByFansId(String fansId);
}
