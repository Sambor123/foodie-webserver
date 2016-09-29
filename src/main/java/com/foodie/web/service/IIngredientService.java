package com.foodie.web.service;

import com.foodie.web.model.Ingredient;

public interface IIngredientService {
	int insert(Ingredient ingredient);

	Ingredient selectByName(String name1);
}
