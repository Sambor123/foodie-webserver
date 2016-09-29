package com.foodie.web.dao;

import com.foodie.web.model.Step;

public interface StepMapper {
    int deleteByPrimaryKey(String id);

    int insert(Step record);

    int insertSelective(Step record);

    Step selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Step record);

    int updateByPrimaryKey(Step record);
}