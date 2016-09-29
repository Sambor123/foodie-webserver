package com.foodie.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodie.web.dao.WorksMapper;
import com.foodie.web.model.Works;
import com.foodie.web.service.IWorksService;

@Service
public class WorksService implements IWorksService{
	
	@Autowired
	private WorksMapper worksMapper;

	@Override
	public Works selectByPrimaryKey(String id) {
		// TODO Auto-generated method stub
		return worksMapper.selectByPrimaryKey(id);
	}

	@Override
	public int insert(Works works) {
		// TODO Auto-generated method stub
		return worksMapper.insert(works);
	}

	@Override
	public List<Works> getAll() {
		// TODO Auto-generated method stub
		return worksMapper.getAll();
	}

	@Override
	public List<Works> selectByUserId(String userId) {
		// TODO Auto-generated method stub
		return worksMapper.selectByUserId(userId);
	}

}
