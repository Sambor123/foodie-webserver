package com.foodie.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.LikeMapper;
import com.foodie.web.model.Like;
import com.foodie.web.service.ILikeService;

@Service
public class LikeService implements ILikeService{
	
	@Autowired
	private LikeMapper likeMapper;
	@Override
	public int insert(Like like) {
		return likeMapper.insert(like);
	}
}
