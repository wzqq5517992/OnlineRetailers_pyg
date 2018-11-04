package com.pinyougou.manager.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import entity.Result;
import util.FastDFSClient;

/**
 * 文件上传Controller
 * 
 * @author Administrator
 *
 */
@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;// 文件服务器地址

	@RequestMapping("/upload")
	public Result upload(HttpServletRequest request ,MultipartFile file) {
		// 1、取文件的扩展名
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		try {
			//设置图片上传路径
			String url = request.getSession().getServletContext().getRealPath("/upload");
	        File  files =new File(url);
	        if (!files.exists()) {
				files.mkdirs();
			}
			System.out.println(url);	        
	       String   urls=url+"\\"+originalFilename;
	        file.transferTo(new File(url+"\\"+originalFilename));
	        //String urls="D:\\wzq.jpg";
	        
			System.out.println(urls);
			return new Result(true, urls);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}
}
