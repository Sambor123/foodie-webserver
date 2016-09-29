package com.foodie.web.dao;

import com.foodie.web.model.Amount;

public interface AmountMapper {
    int deleteByPrimaryKey(String id);

    int insert(Amount record);

    int insertSelective(Amount record);

    Amount selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Amount record);

    int updateByPrimaryKey(Amount record);
}