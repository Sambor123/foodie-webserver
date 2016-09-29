package com.foodie.web.dao;

import com.foodie.web.model.RecipeComment;

public interface RecipeCommentMapper {
    int deleteByPrimaryKey(String id);

    int insert(RecipeComment record);

    int insertSelective(RecipeComment record);

    RecipeComment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(RecipeComment record);

    int updateByPrimaryKey(RecipeComment record);
}