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

import com.foodie.core.entity.Result;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.SessionUtil;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Amount;
import com.foodie.web.model.Collection;
import com.foodie.web.model.Ingredient;
import com.foodie.web.model.Recipe;
import com.foodie.web.model.RecipeComment;
import com.foodie.web.model.User;
import com.foodie.web.model.Works;
import com.foodie.web.service.IAmountService;
import com.foodie.web.service.ICollectionService;
import com.foodie.web.service.IIngredientService;
import com.foodie.web.service.IRecipeCommentService;
import com.foodie.web.service.IRecipeService;
import com.foodie.web.service.IUserService;
import com.foodie.web.service.impl.IngredientService;
import com.foodie.web.service.impl.UserService;

/**
 * 
 * @author Xiongbo
 *
 */
@Controller
@RequestMapping(value = "/recipe")
public class RecipeController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IRecipeService recipeService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IIngredientService ingredientService;
    @Autowired
    private IAmountService amoutService;
    @Autowired
    private IRecipeCommentService recipeCommentService;
    @Autowired
    private ICollectionService collectionService;
    /**
     * 获取指定食谱信息
     * @param recipeId
     * @return
     */
    @RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result selectByPrimaryKey(@PathVariable("id") String recipeId){
    	Result result = new Result();
		if (recipeId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("recipeNotExist");
			result.setTipMsg("菜谱不存在");
			return result;
		}
		Recipe recipe = this.recipeService.selectByPrimaryKey(recipeId);
		if (recipe == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("RecipeNotExist");
			result.setTipMsg("不存在该菜谱");
			return result;
		}
		Map<String, Object> recipeInfo = new HashMap<>();
		recipeInfo.put("name", recipe.getName());
		recipeInfo.put("introduction", recipe.getIntroduction());
		recipeInfo.put("thumbnail", recipe.getThumbnail());
		recipeInfo.put("createTime", recipe.getCreateTime());
		recipeInfo.put("difficult", recipe.getDifficult());
		recipeInfo.put("score", recipe.getScore());
		recipeInfo.put("story", recipe.getStory());
		recipeInfo.put("time", recipe.getTime());
		recipeInfo.put("tips", recipe.getTips());
		recipeInfo.put("viewCount", recipe.getViewCount());
		recipeInfo.put("collectionCount", recipe.getCollectionCount());
		String userId=recipe.getUserId();
		User user=userService.selectByPrimaryKey(userId);
		recipeInfo.put("userNickname", user.getNickname());
		recipeInfo.put("userAvator", user.getAvator());
		result.setData(recipeInfo);
		return result;
    }
    /**
     * 获取热门菜谱
     * @param count
     * @return
     */
    @RequestMapping(value="/new/{count}",method=RequestMethod.GET)
    @ResponseBody
    public Result getNewRecipes(@PathVariable("count") String count){
    	Result result=new Result();
    	int countInt=Integer.parseInt(count);
    	List<Recipe> recipes=recipeService.getNewRecipes(countInt);
    	result.setData(recipes);
		return result;
    }
    /**
     * 获取最新菜谱
     * @param count
     * @return
     */
    @RequestMapping(value="/hot/{count}",method=RequestMethod.GET)
    @ResponseBody
    public Result getHotRecipes(@PathVariable("count") String count){
    	Result result=new Result();
    	int countInt=Integer.parseInt(count);
    	List<Recipe> recipes=recipeService.getHotRecipes(countInt);
    	result.setData(recipes);
		return result;
    }
    /**
     * 上传菜谱
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value="/",method=RequestMethod.POST)
    @ResponseBody
    public Result insert(HttpServletRequest request,HttpSession session){
    	Result result = new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	//获取请求参数
    	String name=request.getParameter("name");
    	String introduction=request.getParameter("introduction");
    	String tips=request.getParameter("tips");
    	String story=request.getParameter("story");
    	String time=request.getParameter("time");
    	String difficult=request.getParameter("difficult");
    	String thumbnail=request.getParameter("thumbnail");
    	
    	if(name==null||introduction==null||time==null||difficult==null||thumbnail==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("lackInfo");
			result.setTipMsg("发表信息不完整");
			return result;
    	}
    	String recipeId = UUIDUtils.getUUID();
    	//材料和量，最少3种，最多10种材料，量不可少
    	String name1=request.getParameter("name1");
    	String amount1=request.getParameter("amount1");
    	if(name1!=null&&amount1!=null){
    		Ingredient ingredient=ingredientService.selectByName(name1);
    		if(ingredient==null){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("ingredientNotEnough");
    			result.setTipMsg("至少三种材料且材料量不能为空");
    			return result;
    		}
 
        	Amount amount=new Amount();
        	String id=UUIDUtils.getUUID();
        	amount.setAmount(amount1);
        	amount.setId(id);
        	amount.setIngredientId(ingredient.getId());
        	amount.setRecipeId(recipeId);
        	amoutService.insert(amount);
        	
    	}else{
    		result.setStatus(Result.FAILED);
			result.setTipCode("ingredientNotEnough");
			result.setTipMsg("至少三种材料且材料量不能为空");
			return result;
    	}
    
    	String name2=request.getParameter("name2");
    	String amount2=request.getParameter("amount2");
    	if(name2!=null&&amount2!=null){
    		Ingredient ingredient=ingredientService.selectByName(name2);
    		if(ingredient==null){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("ingredientNotEnough");
    			result.setTipMsg("至少三种材料且材料量不能为空");
    			return result;
    		}
 
        	Amount amount=new Amount();
        	String id=UUIDUtils.getUUID();
        	amount.setAmount(amount2);
        	amount.setId(id);
        	amount.setIngredientId(ingredient.getId());
        	amount.setRecipeId(recipeId);
        	amoutService.insert(amount);
        	
    	}else{
    		result.setStatus(Result.FAILED);
			result.setTipCode("ingredientNotEnough");
			result.setTipMsg("至少三种材料且材料量不能为空");
			return result;
    	}
    	String name3=request.getParameter("name3");
    	String amount3=request.getParameter("amount3");
    	if(name3!=null&&amount3!=null){
    		Ingredient ingredient=ingredientService.selectByName(name3);
    		if(ingredient==null){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("ingredientNotEnough");
    			result.setTipMsg("至少三种材料且材料量不能为空");
    			return result;
    		}
 
        	Amount amount=new Amount();
        	String id=UUIDUtils.getUUID();
        	amount.setAmount(amount3);
        	amount.setId(id);
        	amount.setIngredientId(ingredient.getId());
        	amount.setRecipeId(recipeId);
        	amoutService.insert(amount);
        	
    	}else{
    		result.setStatus(Result.FAILED);
			result.setTipCode("ingredientNotEnough");
			result.setTipMsg("至少三种材料且材料量不能为空");
			return result;
    	}
    	String name4=request.getParameter("name4");
    	String amount4=request.getParameter("amount4");
    	if(name4!=null&&amount4!=null){
    		Ingredient ingredient=ingredientService.selectByName(name4);
    		if(ingredient!=null){
    			Amount amount=new Amount();
            	String id=UUIDUtils.getUUID();
            	amount.setAmount(amount4);
            	amount.setId(id);
            	amount.setIngredientId(ingredient.getId());
            	amount.setRecipeId(recipeId);
            	amoutService.insert(amount);
    		} 	
    	}
    	String name5=request.getParameter("name5");
    	String amount5=request.getParameter("amount5");
    	if(name5!=null&&amount5!=null){
    		Ingredient ingredient=ingredientService.selectByName(name5);
    		if(ingredient!=null){
    			Amount amount=new Amount();
            	String id=UUIDUtils.getUUID();
            	amount.setAmount(amount5);
            	amount.setId(id);
            	amount.setIngredientId(ingredient.getId());
            	amount.setRecipeId(recipeId);
            	amoutService.insert(amount);
    		} 	
    	}
    	String name6=request.getParameter("name6");
    	String amount6=request.getParameter("amount6");
    	if(name6!=null&&amount6!=null){
    		Ingredient ingredient=ingredientService.selectByName(name6);
    		if(ingredient!=null){
    			Amount amount=new Amount();
            	String id=UUIDUtils.getUUID();
            	amount.setAmount(amount6);
            	amount.setId(id);
            	amount.setIngredientId(ingredient.getId());
            	amount.setRecipeId(recipeId);
            	amoutService.insert(amount);
    		} 	
    	}
    	
		String createTime = STDateUtils.getCurrentTime();
    	Recipe recipe=new Recipe();
    	recipe.setName(name);
    	recipe.setId(recipeId);
    	recipe.setCreateTime(createTime);
    	recipe.setDifficult(difficult);
    	recipe.setIntroduction(introduction);
    	recipe.setStory(story);
    	recipe.setThumbnail(thumbnail);
    	recipe.setTime(time);
    	recipe.setTips(tips);
    	recipe.setUserId(userId);
    	recipe.setScore("暂无评分数据");
    	if(this.recipeService.insert(recipe)!=1){
    		result.setStatus(Result.FAILED);
			result.setTipCode("error");
			result.setTipMsg("发表错误");
			return result;
    	}
    	return result;
    }
    /**
     * 获取所有菜谱信息
     * @return
     */
    @RequestMapping(value="/",method=RequestMethod.GET)
	@ResponseBody
	public Result getAll(){
		Result result=new Result();
		List<Works> works=recipeService.getAll();
		result.setData(works);
		return result;
	}
    /**
     * 评论菜谱
     * @param request
     * @param session
     * @param recipeId
     * @return
     */
    @RequestMapping(value="/comment/{recipeId}",method=RequestMethod.POST)
   	@ResponseBody
   	public Result comment(HttpServletRequest request,HttpSession session,@PathVariable("recipeId") String recipeId){
    	Result result = new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录,请登录后评论");
			return result;
    	}
    	Recipe recipe=recipeService.selectByPrimaryKey(recipeId);
    	if(recipe==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("recipeNotExist");
			result.setTipMsg("菜品不存在");
			return result;
    	}
    	//获取请求参数
    	String content=request.getParameter("content");
    	String parentId=request.getParameter("parentId");
    	
    	RecipeComment recipeComment=new RecipeComment();
    	String id=UUIDUtils.getUUID();
    	String createTime=STDateUtils.getCurrentTime();
    	
    	recipeComment.setId(id);
    	recipeComment.setCreateTime(createTime);
    	recipeComment.setRecipeId(recipeId);
    	recipeComment.setUserId(userId);
    	recipeComment.setContent(content);
    	
    	if(parentId!=null&&!parentId.equals("")){
    		RecipeComment parentComment=recipeCommentService.selectByPrimaryKey(parentId);
    		if(parentComment!=null){
    			recipeComment.setParentId(parentId);
    		}else{
    			result.setStatus(Result.FAILED);
    			result.setTipCode("parentIdNotExist");
    			result.setTipMsg("评论不存在");
    			return result;
    		}
    	}
    	if(recipeCommentService.insert(recipeComment)!=1){
    		result.setStatus(Result.FAILED);
			result.setTipCode("insertError");
			result.setTipMsg("评论失败");
			return result;
    	}
		return result;
    } 
}
