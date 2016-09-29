package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Works;

public interface IWorksService {
	Works selectByPrimaryKey(String id);

	int insert(Works works);
	
	List<Works> getAll();

	List<Works> selectByUserId(String userId);
}
