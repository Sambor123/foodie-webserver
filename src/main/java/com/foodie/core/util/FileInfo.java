/**
 * Date:2015年9月29日下午2:29:21
 * Copyright (c) 2015, songjiesdnu@163.com All Rights Reserved.
 */
package com.foodie.core.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件实体类. <br/>
 * date: 2015年9月29日 下午2:29:21 <br/>
 * @author songjiesdnu@163.com
 */
public class FileInfo implements Serializable{
	private static final long serialVersionUID = 7372896797298552820L;
	//文件内容
	private byte[] fileContent;
	//文件名称
	private String fileName;
	//文件类型
	private String fileType;
	//附加信息（应对以后扩展的需要）
	Map<String, String> attachments = new HashMap<String, String>();
	
	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public Map<String, String> getAttachments() {
		return attachments;
	}
	public void setAttachments(Map<String, String> attachments) {
		this.attachments = attachments;
	}
	
	public void addAttachment(String key, String value){
		this.attachments.put(key, value);
	}
	
	public void removeAttachment(String key){
		this.attachments.remove(key);
	}
}