package com.foodie.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.DishMapper;
import com.foodie.web.model.Dish;
import com.foodie.web.service.IDishService;

@Service
public class DishService implements IDishService{
	@Autowired
	private DishMapper dishMapper;
	
	@Override
	public Dish selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return dishMapper.selectByPrimaryKey(id);
	}

	@Override
	public int insert(Dish dish) {
		// TODO Auto-generated method stub
		return dishMapper.insert(dish);
	}

	@Override
	public List<Dish> getHotRecipes(Integer count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dish> getNewRecipes(Integer count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dish> getAll() {
		// TODO Auto-generated method stub
		return dishMapper.getAll();
	}

	@Override
	public List<Dish> search(String queryString){
		// TODO Auto-generated method stub
		return dishMapper.search(queryString);
	}

	@Override
	public List<Dish> selectByRestaurantId(String id) {
		// TODO Auto-generated method stub
		return dishMapper.selectByRestaurantId(id);
	}

	@Override
	public int delete(Dish dish) {
		// TODO Auto-generated method stub
		String id=dish.getId();
		return dishMapper.deleteByPrimaryKey(id);
	}

}
