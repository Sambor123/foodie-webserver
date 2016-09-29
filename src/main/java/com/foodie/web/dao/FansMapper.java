package com.foodie.web.dao;

import java.util.List;
import java.util.Map;

import com.foodie.web.model.Fans;

public interface FansMapper {
    int deleteByPrimaryKey(String id);

    int insert(Fans record);

    int insertSelective(Fans record);

    Fans selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Fans record);

    int updateByPrimaryKey(Fans record);

	Fans selectByTwoId(Map<String, Object> param);

	List<Fans> selectByUserId(String userId);
	
	List<Fans> selectByFansId(String fansId);
	
}