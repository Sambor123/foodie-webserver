package com.foodie.web.controller;

import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;

import com.foodie.core.entity.ImageResult;
import com.foodie.core.entity.Result;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.SessionUtil;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Like;
import com.foodie.web.model.User;
import com.foodie.web.model.Works;
import com.foodie.web.service.IFileService;
import com.foodie.web.service.ILikeService;
import com.foodie.web.service.IUserService;
import com.foodie.web.service.IWorksService;

/**
 * 作品控制器
 * @author Xiongbo
 *
 */
@Controller
@RequestMapping(value="/works")
public class WorksController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private IWorksService worksService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ILikeService likeService;
	@Autowired
	private IFileService fileService;
	/**
	 * 获取某个指定作品的信息
	 * @param worksId
	 * @return
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result getWorksInfo(@PathVariable("id") String worksId){
		Result result=new Result();
		Works works=worksService.selectByPrimaryKey(worksId);
		String userId=works.getUserId();
		User user=userService.selectByPrimaryKey(userId);
		Map<String, Object> workInfo=new HashMap<>();
		workInfo.put("work", works);
		workInfo.put("user", user);
		result.setData(workInfo);
		return result;
	}
	/**
	 * 发表作品
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public Result Insert(HttpServletRequest request,HttpSession session,@RequestParam(value = "file") MultipartFile fileUpload){
		Result result = new Result();
		//获取用户信息
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	//获取请求参数
    	String dishName=request.getParameter("dishName");
    	String introduction=request.getParameter("introduction");
    	//String thumbnail=request.getParameter("thumbnail");
    	//图片上传
    	ImageResult pictureResult;
		try {
			logger.info("开始提交图片");
			 pictureResult =fileService.uploadPic(fileUpload,"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("图片上传错误");
			return result;
		}
		logger.info("提交图片成功"+pictureResult.getData());
		if(!pictureResult.getStatus().equals(Result.SUCCESS)){
			logger.info("没有图片");
			result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("图片上传失败，请重新上传");
			return result;
		}
    	String id = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
    	Works works=new Works();
    	works.setId(id);
    	works.setDishName(dishName);
    	works.setIntroduction(introduction);
    	works.setThumbnail(pictureResult.getData());
    	works.setUserId(userId);
    	works.setCreateTime(createTime);
    	works.setCommentCount(0);
    	works.setLikeCount(0);
    	works.setViewCount(0);
    	works.setThemeId("暂无主题");
    	if(worksService.insert(works)!=1){
    		result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("发表错误");
			return result;
    	}
    	result.setData(works);
		return result;
	}
	/**
	 * 获取我的所有作品
	 * @return
	 */
	@RequestMapping(value="/me",method=RequestMethod.GET)
	@ResponseBody
	public Result getMyWorks(HttpSession session){
		Result result=new Result();
		//获取用户信息
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
		List<Works> works=worksService.selectByUserId(userId);
		result.setData(works);
		return result;
	}
	/**
	 * 获取作品列表，包括附带用户信息
	 * @return
	 */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public Result getWorksInfo(){
		Result result=new Result();
		
		List<Map<String, Object>> worksInfoList=new ArrayList<>();
		List<Works> works=worksService.getAll();
		for(Works work:works){
			Map<String,Object> worksInfo=new HashMap<>();
			worksInfo.put("id", work.getId());
			worksInfo.put("dishName", work.getDishName());
			worksInfo.put("introduction", work.getIntroduction());
			worksInfo.put("commentCount", work.getCommentCount());
			worksInfo.put("createTime", work.getCreateTime());
			worksInfo.put("likeCount", work.getLikeCount());
			worksInfo.put("themeId", work.getThemeId());
			worksInfo.put("thumbnail", work.getThumbnail());
			worksInfo.put("viewCount", work.getViewCount());
			worksInfo.put("userId", work.getUserId());
			String userId=work.getUserId();
			User user=userService.selectByPrimaryKey(userId);
			worksInfo.put("avator", user.getAvator());
			worksInfo.put("nickname", user.getNickname());
			worksInfo.put("score", user.getScore());
			worksInfoList.add(worksInfo);
		}
		result.setData(worksInfoList);
		return result;
	}
	/**
	 * 点赞
	 * @param worksId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/like/{worksId}",method=RequestMethod.POST)
	@ResponseBody
	public Result like(@PathVariable("worksId") String worksId,HttpSession session){
		Result result = new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录,请登录后评论");
			return result;
    	}
    	if(worksService.selectByPrimaryKey(worksId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("worksNotExist");
			result.setTipMsg("作品不存在");
			return result;
    	}
    	Like like=new Like();
    	String id=UUIDUtils.getUUID();
    	String createTime=STDateUtils.getCurrentTime();
    	like.setId(id);
    	like.setUserId(userId);
    	like.setWorksId(worksId);
    	likeService.insert(like);
    	like.setCreateTime(createTime);
		return result;
	}
}
