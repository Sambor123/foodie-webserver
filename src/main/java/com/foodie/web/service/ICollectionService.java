package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Collection;

public interface ICollectionService {
	int insert(Collection collection);

	Collection selectByPrimaryKey(String collectionId);

	Collection selectByDishAndUser(String dishId, String userId);

	int delete(Collection collection);

	List<Collection> selectByUserId(String userId);

}
