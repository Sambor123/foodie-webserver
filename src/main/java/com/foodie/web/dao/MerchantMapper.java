package com.foodie.web.dao;

import com.foodie.web.model.Merchant;

public interface MerchantMapper {
    int deleteByPrimaryKey(String id);

    int insert(Merchant record);

    int insertSelective(Merchant record);

    Merchant selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Merchant record);

    int updateByPrimaryKey(Merchant record);
}