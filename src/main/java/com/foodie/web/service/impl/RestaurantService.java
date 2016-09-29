package com.foodie.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.RestaurantMapper;
import com.foodie.web.model.Restaurant;
import com.foodie.web.service.IRestaurantService;

@Service
public class RestaurantService implements IRestaurantService{

	@Autowired
	private RestaurantMapper restaurantMapper;
	@Override
	public Restaurant selectByAccountName(String accountName) {
		// TODO Auto-generated method stub
		return restaurantMapper.selectByAccountName(accountName);
	}
//	@Override
//	public Restaurant selectByPhone(String phone) {
//		// TODO Auto-generated method stub
//		return restaurantMapper.selectByPhone(phone);
//	}
	@Override
	public Restaurant selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return restaurantMapper.selectByPrimaryKey(id);
	}
	@Override
	public List<Restaurant> getAll() {
		// TODO Auto-generated method stub
		return restaurantMapper.getAll();
	}
	@Override
	public int insert(Restaurant restaurant) {
		// TODO Auto-generated method stub
		return restaurantMapper.insert(restaurant);
	}
	
}
