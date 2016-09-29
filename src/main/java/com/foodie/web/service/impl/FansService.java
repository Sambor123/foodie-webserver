package com.foodie.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.FansMapper;
import com.foodie.web.model.Fans;
import com.foodie.web.service.IFansService;

@Service
public class FansService implements IFansService{
	@Autowired
	private FansMapper fansMapper;
	@Override
	public int insert(Fans fans) {
		// TODO Auto-generated method stub
		return fansMapper.insert(fans);
	}
	@Override
	public Fans selectByTwoId(String userId, String fansId) {
		Map<String, Object> param=new HashMap<>();
		param.put("userId", userId);
		param.put("fansId", fansId);
		// TODO Auto-generated method stub
		return fansMapper.selectByTwoId(param);
	}
	@Override
	public int delete(Fans fans) {
		// TODO Auto-generated method stub
		String fansId=fans.getId();
		return fansMapper.deleteByPrimaryKey(fansId);
	}
	@Override
	public List<Fans> selectByUserId(String userId) {
		// TODO Auto-generated method stub
		return fansMapper.selectByUserId(userId);
	}
	@Override
	public List<Fans> selectByFansId(String fansId) {
		// TODO Auto-generated method stub
		return fansMapper.selectByFansId(fansId);
	}
	
	

}
