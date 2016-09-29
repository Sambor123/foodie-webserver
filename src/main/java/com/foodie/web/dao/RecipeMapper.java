package com.foodie.web.dao;

import java.util.List;

import com.foodie.web.model.Recipe;
import com.foodie.web.model.Works;

public interface RecipeMapper {
    int deleteByPrimaryKey(String id);

    int insert(Recipe record);

    int insertSelective(Recipe record);

    Recipe selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Recipe record);

    int updateByPrimaryKey(Recipe record);

	List<Recipe> getHotRecipes(Integer count);

	List<Recipe> getNewRecipes(Integer count);

	List<Works> getAll();
}