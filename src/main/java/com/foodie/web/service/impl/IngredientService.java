package com.foodie.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.IngredientMapper;
import com.foodie.web.model.Ingredient;
import com.foodie.web.service.IIngredientService;

@Service
public class IngredientService implements IIngredientService{
	@Autowired
	private IngredientMapper ingredientMapper;
	@Override
	public int insert(Ingredient ingredient) {
		// TODO Auto-generated method stub
		return ingredientMapper.insert(ingredient);
	}
	@Override
	public Ingredient selectByName(String name) {
		// TODO Auto-generated method stub
		return ingredientMapper.selectByName(name);
	}
}
