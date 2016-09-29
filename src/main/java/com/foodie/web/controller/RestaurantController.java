package com.foodie.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.foodie.core.entity.Result;
import com.foodie.core.util.MD5Utils;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Dish;
import com.foodie.web.model.Restaurant;
import com.foodie.web.model.User;
import com.foodie.web.service.IRestaurantService;

@Controller
@RequestMapping(value="/restaurant")
public class RestaurantController {
	
	private Logger logger = LoggerFactory.getLogger(RestaurantController.class);
	
	@Autowired
	private IRestaurantService restaurantService;
	
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public Result register(HttpServletRequest request) {
		Result result = new Result();
		String accountName=request.getParameter("accountName");
		String accountPassword=request.getParameter("accountPassword");
		String restaurantName=request.getParameter("restaurantName");
		String address=request.getParameter("address");
		String introduction=request.getParameter("introduction");
		String averagePrice=request.getParameter("averagePrice");
		String phone=request.getParameter("phone");
		if(restaurantService.selectByAccountName(accountName)!=null){
			result.setStatus(Result.FAILED);
			result.setTipMsg("账户名已存在");
			result.setTipCode("nameExist");
			return result;
		}
		if(accountName==null||accountName.equals("")||accountPassword==null||accountPassword.equals("")||restaurantName==null||restaurantName.equals("")||address==null||address.equals("")){
			result.setStatus(Result.FAILED);
			result.setTipMsg("注册信息不完整");
			result.setTipCode("NotValidInfo");
			return result;
		}
		Restaurant restaurant=new Restaurant();
		String id = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
		restaurant.setId(id);
		restaurant.setAccountName(accountName);
		restaurant.setAccountPassword(accountPassword);
		restaurant.setAddress(address);
		restaurant.setIntroduction(introduction);
		restaurant.setRestaurantName(restaurantName);
		restaurant.setRegisterTime(createTime);
		restaurant.setAveragePrice(averagePrice);
		restaurant.setPictureLarge("http://bjtu-foodie.oss-cn-shanghai.aliyuncs.com/3.jpg");
		restaurant.setPictureSmall("http://bjtu-foodie.oss-cn-shanghai.aliyuncs.com/3.jpg");
		restaurant.setScore("4.0");
		restaurant.setKeyWord("");
		restaurant.setOther("");
		restaurant.setPhone(phone);
		restaurant.setRestaurantType("普通");
		restaurant.setServeTime("8:00AM-22:00PM");
		restaurant.setStatus("正常");
		if(restaurantService.insert(restaurant)!=1){
			result.setStatus(Result.FAILED);
			result.setTipMsg("注册失败");
			result.setTipCode("RegisterFail");
			return result;
		}
		result.setData(restaurant);
		return result;
	}
	/**
	 * 商家登录
	 * @param request
	 * @param session
	 * @param accountName
	 * @param accountPassword
	 * @return
	 */
	@RequestMapping(value = "/login",method=RequestMethod.POST)
	@ResponseBody
	public Result login(HttpServletRequest request,HttpSession session,
			@RequestParam("accountName") String accountName,
			@RequestParam("accountPassword") String accountPassword
			) {
		logger.info("进入商户login方法");
		Result result = new Result();
		//去掉用户名、密码前后的空格
		accountName = accountName.trim();
		accountPassword = accountPassword.trim();
		Restaurant restaurant=restaurantService.selectByAccountName(accountName);
		//Restaurant restaurant=restaurantService.selectByPhone(phone);
		//Restaurant restaurant=restaurantService.selectByPrimaryKey(id);
		if(restaurant==null){
			result.setStatus(Result.FAILED);
			result.setTipMsg("账户未注册");
			return result;
		}
		if(!restaurant.getAccountPassword().equals(accountPassword)){
			result.setStatus(Result.FAILED);
			result.setTipMsg("账户密码错误");
			return result;
		}
		request.getSession().setAttribute("restaurantId", restaurant.getId());
		logger.info("login成功");
		//result.setStatus("!");
		result.setData(restaurant);
		return result;
	}
	/**
	 * 获取所有商家信息
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public Result getAll(){
		Result result=new Result();
//    	List<Map<String, Object>> restaurantList=new ArrayList<>();
//		List<Restaurant> restaurants=restaurantService.getAll();
//		for(Restaurant restaurant:restaurants){
//			Map<String,Object> restaurantInfo=new HashMap<>();
			
//			restaurantInfo.put("address", restaurant.getAddress());
//			restaurantInfo.put("averagePrice", restaurant.getAveragePrice());
//			restaurantInfo.put("id", restaurant.getId());
//			restaurantInfo.put("introduction", restaurant.getIntroduction());
//			restaurantInfo.put("keyWord", restaurant.getKeyWord());
//			restaurantInfo.put("status", restaurant.getStatus());
//			restaurantInfo.put("serveTime", restaurant.getServeTime());
//			restaurantInfo.put("score", restaurant.getScore());
//			restaurantInfo.put("restaurantType", restaurant.getRestaurantType());
//			restaurantInfo.put("restaurantName", restaurant.getRestaurantName());
//			restaurantInfo.put("resgisterTime", restaurant.getRegisterTime());
//			restaurantInfo.put("pictureSmall", restaurant.getPictureSmall());
//			restaurantInfo.put("pictureLarge", restaurant.getPictureLarge());
//			restaurantInfo.put("phone", restaurant.getPhone());
//			restaurantInfo.put("other", restaurant.getOther());
//			
//			restaurantList.add(restaurantInfo);
//		}
		List<Restaurant> restaurants=restaurantService.getAll();
		result.setData(restaurants);
		return result;
	}
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result getOne(@PathVariable("id") String id){
		Result result=new Result();
		Restaurant restaurant=restaurantService.selectByPrimaryKey(id);
		if(restaurant==null){
			result.setStatus(Result.FAILED);
			result.setTipCode("NotExsit");
			result.setTipMsg("账户不存在");
		}
		result.setData(restaurant);
		return result;
	}
}
