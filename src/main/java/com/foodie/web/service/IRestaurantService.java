package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Restaurant;

public interface IRestaurantService {

//	Restaurant selectByAccountName(String accountName);
//
//	Restaurant selectByPhone(String phone);

	Restaurant selectByPrimaryKey(String id);

	Restaurant selectByAccountName(String accountName);

	List<Restaurant> getAll();

	int insert(Restaurant restaurant);
}
