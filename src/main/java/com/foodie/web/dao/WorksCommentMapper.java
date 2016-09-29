package com.foodie.web.dao;

import com.foodie.web.model.WorksComment;

public interface WorksCommentMapper {
    int deleteByPrimaryKey(String id);

    int insert(WorksComment record);

    int insertSelective(WorksComment record);

    WorksComment selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WorksComment record);

    int updateByPrimaryKey(WorksComment record);
}