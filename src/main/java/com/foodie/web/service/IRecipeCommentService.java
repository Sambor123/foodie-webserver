package com.foodie.web.service;

import com.foodie.web.model.RecipeComment;

public interface IRecipeCommentService {
	int insert(RecipeComment recipeComment);

	RecipeComment selectByPrimaryKey(String id);
}
