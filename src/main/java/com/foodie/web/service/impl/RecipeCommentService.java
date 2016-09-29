package com.foodie.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.RecipeCommentMapper;
import com.foodie.web.model.RecipeComment;
import com.foodie.web.service.IRecipeCommentService;
import com.foodie.web.service.IRecipeService;

@Service
public class RecipeCommentService implements IRecipeCommentService{

	@Autowired
	private RecipeCommentMapper recipeCommentMapper;
	
	@Override
	public int insert(RecipeComment recipeComment) {
		// TODO Auto-generated method stub
		return recipeCommentMapper.insert(recipeComment);
	}

	@Override
	public RecipeComment selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return recipeCommentMapper.selectByPrimaryKey(id);
	}

}
