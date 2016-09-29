/**
 * Date:2015年7月29日下午10:24:47
 * Copyright (c) 2015, songjiesdnu@163.com All Rights Reserved.
 */
package com.foodie.core.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密. <br/>
 * date: 2015年7月29日 下午10:24:47 <br/>
 *
 * @author songjiesdnu@163.com
 * @version 
 * @since JDK 1.6
 */
public class MD5Utils {
	public static String encrypt(String input){
		if(input == null){
			return null;
		}
		String md5 = null;
		//Create MessageDigest object for MD5
        MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("无法进行MD5加密", e);
		}
        //Update input string in message digest
        digest.update(input.getBytes(), 0, input.length());
        //Converts message digest value in base 16 (hex) 
        md5 = new BigInteger(1, digest.digest()).toString(16);
		return md5;
	}
}

