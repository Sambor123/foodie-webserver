package com.foodie.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foodie.core.entity.Result;
import com.foodie.web.service.ICollectionService;

@Controller
@RequestMapping(value="/collection")
public class CollectionController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private ICollectionService collectionService;
	
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result selectByPrimaryKey(@PathVariable("id") String collectionId){
		Result result=new Result();
		collectionService.selectByPrimaryKey(collectionId);
		return result;
	}
	
}
