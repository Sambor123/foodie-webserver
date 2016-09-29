/**
 * Date:2015年9月29日上午10:50:34
 * Copyright (c) 2015, songjiesdnu@163.com All Rights Reserved.
 */
package com.foodie.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * Function: OSS文件上传和下载. <br/>
 * date: 2015年9月29日 上午10:50:34 <br/>
 *
 * @author songjiesdnu@163.com
 */
public class AliyunFileManager {
	private Logger logger = LoggerFactory.getLogger(AliyunFileManager.class);
	
	private String accessKeyId;
	private String accessKeySecret;
	private String endpoint;
	private String bucketName;
	
	public String getAccessKeyId(){
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	private OSSClient getOSSClient(){
		ClientConfiguration conf = new ClientConfiguration(); 
		conf.setSocketTimeout(0);
		return new OSSClient(endpoint,accessKeyId, accessKeySecret, conf);
	}
	
	/**
	 * 上传文件
	 * @author songjiesdnu@163.com
	 * @param filename：文件名称
	 * @param content：文件的内容，用字节数组存储
	 * @return 文件在OSS中的唯一标识，即fileId
	 */
	public String upload(String filename, byte[] content){
		logger.debug("upload file to OSS--start");
		if(content == null  ||  content.length == 0){
			throw new IllegalArgumentException("文件内容不能为空");
		}
		OSSClient client = this.getOSSClient();
		String key = UUIDUtils.getUUID();
		
		ObjectMetadata meta = new ObjectMetadata();
		Date expireDate = new Date(new Date().getTime() + 3153600000000L);// 过期时间设置为一百年后
		meta.setExpirationTime(expireDate);
	    Map<String, String> userMetadata = new HashMap<String, String>();
	    if(filename == null  ||  filename.equals("")){
	    	filename = key;
	    }
	    logger.debug("filename:" + filename);
	    userMetadata.put("filename", filename);
	    meta.setUserMetadata(userMetadata);
		//开始Multipart Upload
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, key, meta);
		InitiateMultipartUploadResult initiateMultipartUploadResult = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		
		logger.debug("UploadId: " + initiateMultipartUploadResult.getUploadId());
		
		//设置每块为 5M
		final int partSize = 1024 * 1024 * 5;
		//计算分块数目
		int partCount = (int) (content.length / partSize);
		if (content.length % partSize != 0){
		    partCount++;
		}
		//新建一个List保存每个分块上传后的ETag和PartNumber
		List<PartETag> partETags = new ArrayList<PartETag>();
		for(int i = 0; i < partCount; i++){
			InputStream partIs = new ByteArrayInputStream(content);
		    //计算每个分块的大小
		    int size = partSize < content.length-i*partSize ?
		            partSize : content.length-i*partSize;
		    byte[] copy = new byte[size];
		    System.arraycopy(content, partSize*i, copy, 0, size);
		    //创建UploadPartRequest，上传分块
		    UploadPartRequest uploadPartRequest = new UploadPartRequest();
		    uploadPartRequest.setBucketName(bucketName);
		    uploadPartRequest.setKey(key);
		    uploadPartRequest.setUploadId(initiateMultipartUploadResult.getUploadId());
		    uploadPartRequest.setInputStream(partIs);
		    uploadPartRequest.setPartSize(size);
		    uploadPartRequest.setPartNumber(i + 1);
		    UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
		    //将返回的PartETag保存到List中
		    partETags.add(uploadPartResult.getPartETag());
		}
		CompleteMultipartUploadRequest completeMultipartUploadRequest =
		        new CompleteMultipartUploadRequest(bucketName,key, initiateMultipartUploadResult.getUploadId(), partETags);
		//完成分块上传
		CompleteMultipartUploadResult completeMultipartUploadResult =
		        client.completeMultipartUpload(completeMultipartUploadRequest);
		logger.debug("ETag:" + completeMultipartUploadResult.getETag());
		logger.debug("文件的key：" + key);
		logger.debug("upload file to OSS--end");
	    return key;
	}
	
	/**
	 * 下载文件
	 * @author songjiesdnu@163.com
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public FileInfo download(String fileId) throws IOException{
		logger.debug("download file from OSS--start");
		if(fileId == null  ||  fileId.equals("")){
			throw new IllegalArgumentException("fileId不能为null或者空字符串");
		}
		OSSClient client = this.getOSSClient();
		OSSObject ossObject = client.getObject(bucketName, fileId);
		FileInfo fileInfo = new FileInfo();
		InputStream is = ossObject.getObjectContent();
		byte[] content = IOUtils.readStreamAsByteArray(is);
		checkContent(content);
		ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
		String fileName = objectMetadata.getUserMetadata().get("filename");
		fileInfo.setFileContent(content);
		fileInfo.setFileName(fileName);
		
		logger.debug("download file from OSS--end");
		return fileInfo;
	}
	
	/**
	 * 检查文件内容
	 * @author songjiesdnu@163.com
	 * @param content
	 */
	private void checkContent(byte[] content){
		boolean exist = true;
		if(content == null){
			exist = false;
		}else{
			if(content.length == 3){
				if(new String(content).equals("404")){
					exist = false;
				}
			}
		}
		if(!exist){
			throw new IllegalArgumentException("文件不存在");
		}
	}
}