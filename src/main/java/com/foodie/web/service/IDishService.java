package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Dish;


public interface IDishService {
	Dish selectByPrimaryKey(String id);
	
	int insert(Dish dish);
	
	List<Dish> getHotRecipes(Integer count);

	List<Dish> getNewRecipes(Integer count);

	List<Dish> getAll();

	List<Dish> search(String queryString);

	List<Dish> selectByRestaurantId(String id);

	int delete(Dish dish);
}
