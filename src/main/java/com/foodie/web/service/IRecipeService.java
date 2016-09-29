package com.foodie.web.service;

import java.util.List;

import com.foodie.web.model.Recipe;
import com.foodie.web.model.Works;

public interface IRecipeService {
	Recipe selectByPrimaryKey(String id);
	
	int insert(Recipe recipe);
	
	List<Recipe> getHotRecipes(Integer count);

	List<Recipe> getNewRecipes(Integer count);

	List<Works> getAll();
}
