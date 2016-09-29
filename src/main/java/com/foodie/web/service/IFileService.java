package com.foodie.web.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.foodie.core.entity.ImageResult;
import com.foodie.core.entity.Result;

public interface IFileService {
	ImageResult uploadPic(@RequestParam(value = "upload") MultipartFile fileUpload,String path) throws IOException;
	
	Map<String, String> ckeditorUpload(HttpServletRequest request, Model model,
			@RequestParam(value = "upload") MultipartFile fileUpload,
			HttpServletResponse response)throws IOException;
	
	String validateUploadFile(MultipartFile fileUpload, String[] allowedFileTypes);
	
	String saveFile(MultipartFile fileUpload)throws IOException ;
	
	String getOSSBucketUrl();
}
