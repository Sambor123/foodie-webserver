package com.foodie.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.CollectionMapper;
import com.foodie.web.model.Collection;
import com.foodie.web.service.ICollectionService;

@Service
public class CollectionService implements ICollectionService{

	@Autowired
	private CollectionMapper collectionMapper;
	@Override
	public int insert(Collection collection) {
		// TODO Auto-generated method stub
		return collectionMapper.insert(collection);
	}
	@Override
	public Collection selectByPrimaryKey(String collectionId) {
		// TODO Auto-generated method stub
		return collectionMapper.selectByPrimaryKey(collectionId);
	}
	@Override
	public Collection selectByDishAndUser(String dishId, String userId) {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<String, Object>();
		//以name字段升序排序，
		params.put("dishId", dishId);
		params.put("userId", userId);
		//分页查询教师信息
		return collectionMapper.selectByDishAndUser(params);
	}
	@Override
	public int delete(Collection collection) {
		// TODO Auto-generated method stub
		String id=collection.getId();
		return collectionMapper.deleteByPrimaryKey(id);
	}
	@Override
	public List<Collection> selectByUserId(String userId) {
		// TODO Auto-generated method stub
		return collectionMapper.selectByUserId(userId);
	}

}
