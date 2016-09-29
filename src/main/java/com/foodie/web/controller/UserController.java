package com.foodie.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.tags.UserTag;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.foodie.core.entity.ImageResult;
import com.foodie.core.entity.Result;
import com.foodie.core.util.MD5Utils;
import com.foodie.core.util.STDateUtils;
import com.foodie.core.util.SessionUtil;
import com.foodie.core.util.UUIDUtils;
import com.foodie.web.model.Collection;
import com.foodie.web.model.Fans;
import com.foodie.web.model.User;
import com.foodie.web.service.IFansService;
import com.foodie.web.service.IFileService;
import com.foodie.web.service.IUserService;
import com.foodie.web.service.IWorksService;
import com.foodie.web.service.impl.WorksService;

/**
 * 用户控制器
 * 
 * @author Xiongbo
 * @since 2016年7月3日 下午3:54:00
 **/
@Controller
@RequestMapping(value = "/user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserService userService;
    @Autowired
    private IFansService fansService;
    @Autowired
    private IWorksService worksService;
    @Autowired
    private IFileService fileService;
    /**
	 * 提交注册时检查
	 * 
	 * @param request
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public Result register(HttpServletRequest request) {
		Result result = new Result();

		String nickname = request.getParameter("nickname");
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		String passwordAgain = request.getParameter("passwordAgain");
		if (nickname==null||phone == null || password == null
				|| passwordAgain == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("lackRegisterInfo");
			result.setTipMsg("注册信息不完整");
			return result;
		}
		if (!this.phoneNoCheck(phone)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("mobileNoIllegal");
			result.setTipMsg("手机号不符要求");
			return result;
		}
		if (!this.phoneNoReg(phone)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("mobileNoHasBeenRegistered");
			result.setTipMsg("手机号已注册");
			return result;
		}
		if (!this.passwordCheck(password, passwordAgain)) {
			result.setStatus(Result.FAILED);
			result.setTipCode("passwordNotEqual");
			result.setTipMsg("两次输入密码不一致");
			return result;
		}

		User user = new User();
		String id = UUIDUtils.getUUID();
		String salt = UUIDUtils.getUUID();
		String createTime = STDateUtils.getCurrentTime();
		
		user.setId(id);
		user.setUsername(phone);
		user.setPhone(phone);
		user.setNickname(nickname);
		user.setPassword(MD5Utils.encrypt(password + salt));
		user.setCreateTime(createTime);
		user.setSalt(salt);
		user.setAvator("http://bjtu-foodie.oss-cn-shanghai.aliyuncs.com/head/head0.jpeg");
		this.userService.insert(user);
		shiroLogin(phone, password);
		result.setData(user);
		return result;
	}
    /**
	 * 登录(用户名、手机号或邮箱)
	 * 
	 * @param request
	 * @param username
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/login",method=RequestMethod.POST)
	@ResponseBody
	public Result login(HttpServletRequest request,
			@RequestParam("phone") String phone,
			@RequestParam("password") String password
			) {
		logger.debug("进入login方法");
		Result result = new Result();
		//去掉用户名、密码前后的空格
		phone = phone.trim();
		password = password.trim();
		UsernamePasswordToken token = new UsernamePasswordToken(phone, password);
		logger.debug("为了验证登录用户而封装的token为:" + JSON.toJSONString(token));
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		try {
			// 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
			// 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
			// 所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
			logger.debug("对用户[{}]进行登录验证..验证开始", phone);
			currentUser.login(token);
			logger.debug("对用户[{}]进行登录验证..验证通过", phone);
		} catch (UnknownAccountException uae) {
			logger.warn("对用户[{}]进行登录验证..验证未通过,未知账号", phone);
			logger.warn("未知账户", uae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("账户未注册");
		} catch (IncorrectCredentialsException ice) {
			logger.warn("对用户[{}]进行登录验证..验证未通过,错误的凭证", phone);
			logger.warn("密码错误", ice);
			result.setStatus(Result.FAILED);
			result.setTipMsg("用户名或密码错误");
		} catch (LockedAccountException lae) {
			logger.warn("对用户[{}]进行登录验证..验证未通过,账号已锁定", phone);
			logger.warn("账号已锁定", lae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("账号已锁定");
		} catch (ExcessiveAttemptsException eae) {
			logger.warn("对用户[{}]进行登录验证..验证未通过,错误次数过多", phone);
			logger.warn("用户名或密码错误次数太多", eae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("请输入正确的图片验证码");
			result.setTipCode("showPictureCode");
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			logger.warn("对用户[{}]进行登录验证..验证未通过,堆栈轨迹如下", phone);
			logger.warn("用户名或密码不正确", ae);
			result.setStatus(Result.FAILED);
			result.setTipMsg("用户名或密码不正确");
		}
		//验证是否登录成功
		if (currentUser.isAuthenticated()) {
			logger.info("用户[{}]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)", phone);
			User user=userService.selectByPhone(currentUser.getPrincipal().toString());
			result.setData(user);
		} else {
			token.clear();
		}
		return result;
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 */
	@RequestMapping(value="/logOut",method=RequestMethod.GET)
	@ResponseBody
	public Result logOut() {
		SecurityUtils.getSubject().logout();
		logger.info("退出登录");
		Result result = new Result();
		return result;
	}
	
	@RequestMapping(value="/me",method=RequestMethod.GET)
	@ResponseBody
	public Result showUserInfo(HttpSession session){
		Result result = new Result();
		String userId = SessionUtil.getUserId(session);
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotLogin");
			result.setTipMsg("用户未登陆");
			return result;
		}
		User user = this.userService.selectByPrimaryKey(userId);
		if (user == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("visitorNotExist");
			result.setTipMsg("不存在该用户");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * 查看指定用户信息
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Result selectByPrimaryKey(@PathVariable("id") String userId){
		Result result = new Result();
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotLogin");
			result.setTipMsg("用户不存在");
			return result;
		}
		User user = this.userService.selectByPrimaryKey(userId);
		if (user == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("userNotExist");
			result.setTipMsg("不存在该用户");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * 修改我的资料
	 * @return
	 */
	@RequestMapping(value="/me/modify",method=RequestMethod.POST)
	@ResponseBody
	public Result modifyUserInfo(HttpServletRequest request,HttpSession session,@RequestParam(value = "file") MultipartFile fileUpload){
		Result result=new Result();
		String userId = SessionUtil.getUserId(session);
		if (userId == null) {
			result.setStatus(Result.FAILED);
			result.setTipCode("NotLogin");
			result.setTipMsg("用户未登陆");
			return result;
		}
		String nickname = request.getParameter("nickname");
		String password = request.getParameter("password");
		String passwordAgain = request.getParameter("passwordAgain");
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
		User user=userService.selectByPrimaryKey(userId);
		
		user.setAvator(pictureResult.getData());
		if(nickname!=null&&!nickname.equals("")){
			user.setNickname(nickname);
		}
		if(password!=null&&!password.equals("")){
			user.setPassword(password);
			
		}
		if(passwordAgain!=null&&!passwordAgain.equals("")){
			if(!passwordCheck(password, passwordAgain)){
				result.setStatus(Result.FAILED);
				result.setTipCode("passwordDiffirent");
				result.setTipMsg("用户未密码不一致");
				return result;
			}
		}
		if(userService.updateByPrimaryKeySelective(user)!=1){
			result.setStatus(Result.FAILED);
			result.setTipCode("updateError");
			result.setTipMsg("修改失败");
			return result;
		}
		result.setData(user);
		return result;
	}
	/**
	 * 关注用户,取消关注
	 * @param userId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="/{id}/togglefollow/",method=RequestMethod.POST)
	@ResponseBody
	public Result follow(@PathVariable("id") String userId,HttpSession session){
		Result result=new Result();
		String myUserId = SessionUtil.getUserId(session);
    	if(myUserId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	if(userService.selectByPrimaryKey(userId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("userNotExist");
			result.setTipMsg("用户不存在");
			return result;
    	}
    	if(fansService.selectByTwoId(userId,myUserId)!=null){
    		Fans fans=fansService.selectByTwoId(userId, myUserId);
    		if(fansService.delete(fans)!=1){
    			result.setStatus(Result.FAILED);
    			result.setTipCode("unFollowFail");
    			result.setTipMsg("取消关注失败");
    			return result;
    		}else{
    			result.setStatus(Result.SUCCESS);
    			result.setTipCode("unFollow");
    			result.setTipMsg("取消关注成功");
    			result.setData(fans);
    			return result;
    		}
    	}else{
    		Fans fans=new Fans();
        	String id=UUIDUtils.getUUID();
        	String followTime=STDateUtils.getCurrentTime();
        	fans.setId(id);
        	fans.setFansId(myUserId);
        	fans.setUserId(userId);
        	fans.setFollowTime(followTime);
        	if(fansService.insert(fans)!=1){
        		result.setStatus(Result.FAILED);
    			result.setTipCode("followFail");
    			result.setTipMsg("关注失败");
    			return result;
        	}else{
        		result.setStatus(Result.SUCCESS);
    			result.setTipCode("follow");
    			result.setTipMsg("关注成功");
    			result.setData(fans);
    			return result;
        	}
    	}
	}
	
	@RequestMapping(value="/{id}/isfollow/",method=RequestMethod.GET)
	@ResponseBody
	public Result hasFollow(@PathVariable("id") String userId,HttpSession session){
		Result result=new Result();
    	String fansId = SessionUtil.getUserId(session);
    	if(fansId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	if(userService.selectByPrimaryKey(userId)==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notExist");
			result.setTipMsg("你关注的用户不存在");
			return result;
    	}
    	
    	Fans fans=fansService.selectByTwoId(userId, fansId);
    	if(fans==null){
    		result.setStatus(Result.SUCCESS);
			result.setTipCode("notFollow");
			result.setTipMsg("未关注");
			return result;
    	}
    	result.setTipCode("follow");
    	result.setData(fans);
		return result;
	}
	@RequestMapping(value="/fanscount/",method=RequestMethod.GET)
	@ResponseBody
	public Result getFansCount(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByUserId(userId);
    	int count=fanses.size();
		result.setData(count);
		return result;
		
	}
	@RequestMapping(value="/followcount/",method=RequestMethod.GET)
	@ResponseBody
	public Result getFollowCount(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByFansId(userId);
    	int count=fanses.size();
		result.setData(count);
		return result;
		
	}
	@RequestMapping(value="/allfans/",method=RequestMethod.GET)
	@ResponseBody
	public Result getAllFans(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByUserId(userId);
    	if(fanses==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("null");
			result.setTipMsg("你没有任何粉丝");
			return result;
    	}else{
    		List<User> users=new ArrayList<>();
    		for(Fans fans:fanses){
    			User user=userService.selectByPrimaryKey(fans.getFansId());
    			users.add(user);
    		}
    		result.setData(users);
    		return result;
    	}	
	}
	@RequestMapping(value="/allfollow/",method=RequestMethod.GET)
	@ResponseBody
	public Result getAllFollow(HttpSession session){
		Result result=new Result();
    	String userId = SessionUtil.getUserId(session);
    	if(userId==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("notLogin");
			result.setTipMsg("未登录");
			return result;
    	}
    	List<Fans> fanses=fansService.selectByFansId(userId);
    	if(fanses==null){
    		result.setStatus(Result.FAILED);
			result.setTipCode("null");
			result.setTipMsg("你没有关注任何用户");
			return result;
    	}else{
    		List<User> users=new ArrayList<>();
    		for(Fans fans:fanses){
    			User user=userService.selectByPrimaryKey(fans.getUserId());
    			users.add(user);
    		}
    		result.setData(users);
    		return result;
    	}	
		
	}
	private boolean nameCheck(String name) {
		Pattern patternName = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]{2,20}$");
		Matcher matcherName = patternName.matcher(name);
		return matcherName.matches();
	}
	
	private boolean phoneNoCheck(String mobileNo) {
		Pattern patternMobileNo = Pattern.compile("^1[34578]\\d{9}$");
		Matcher matcherMobileNo = patternMobileNo.matcher(mobileNo);
		return matcherMobileNo.matches();
	}

	private boolean passwordCheck(String password, String passwordAgain) {
		return (password.length() >= 6 && password.length() <= 20 && password
				.equals(passwordAgain));
	}
	private boolean nameReg(String username) {
		User user = this.userService.selectByUsername(username);
		return (user == null);
	}
	private boolean phoneNoReg(String phone) {
		User user = this.userService.selectByPhone(phone);
		return (user == null);
	}
	/**
	 * shiro登录
	 * @param name
	 * @param password
	 */
	private void shiroLogin(String name, String password){
		UsernamePasswordToken token = new UsernamePasswordToken(name, password);
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.login(token);
	}
}
