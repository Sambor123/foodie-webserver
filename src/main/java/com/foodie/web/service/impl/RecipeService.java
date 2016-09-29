package com.foodie.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.RecipeMapper;
import com.foodie.web.model.Recipe;
import com.foodie.web.model.Works;
import com.foodie.web.service.IRecipeService;

@Service
public class RecipeService implements IRecipeService{
	
	@Autowired
	private RecipeMapper recipeMapper;
	@Override
	public Recipe selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return recipeMapper.selectByPrimaryKey(id);
	}

	@Override
	public int insert(Recipe recipe) {
		// TODO Auto-generated method stub
		return recipeMapper.insert(recipe);
	}

	@Override
	public List<Recipe> getHotRecipes(Integer count) {
		// TODO Auto-generated method stub
		return recipeMapper.getHotRecipes(count);
	}

	@Override
	public List<Recipe> getNewRecipes(Integer count) {
		// TODO Auto-generated method stub
		return recipeMapper.getNewRecipes(count);
	}

	@Override
	public List<Works> getAll() {
		// TODO Auto-generated method stub
		return recipeMapper.getAll();
	}

}
