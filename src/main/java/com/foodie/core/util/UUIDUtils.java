/**
 * Date:2015年7月30日下午11:37:30
 * Copyright (c) 2015, songjiesdnu@163.com All Rights Reserved.
 */
package com.foodie.core.util;

import java.util.UUID;

/**
 * UUID工具类. <br/>
 * date: 2015年7月30日 下午11:37:30 <br/>
 *
 * @author songjiesdnu@163.com
 */
public class UUIDUtils {
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}
}

