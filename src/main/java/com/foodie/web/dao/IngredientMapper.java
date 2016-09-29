package com.foodie.web.dao;

import com.foodie.web.model.Ingredient;

public interface IngredientMapper {
    int deleteByPrimaryKey(String id);

    int insert(Ingredient record);

    int insertSelective(Ingredient record);

    Ingredient selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Ingredient record);

    int updateByPrimaryKey(Ingredient record);

	Ingredient selectByName(String name);
}