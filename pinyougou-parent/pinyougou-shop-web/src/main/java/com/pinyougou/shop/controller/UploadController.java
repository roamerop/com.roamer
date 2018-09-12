package com.pinyougou.shop.controller;

import java.io.IOException;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.DeleteFileUtils;
import util.FastDFSClient;

@RestController
public class UploadController {
	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;// 文件服务器地址

	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		// 获取原始名
		String originalFilename = file.getOriginalFilename();
		// 获取后缀
		String substring = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		FastDFSClient fastDFSClient;
		try {
			fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			String path = fastDFSClient.uploadFile(file.getBytes(), substring);
			// group1/M00/00/00/wKgZhVtpSTuAM5jPAAcsqOIMfgQ816.jpg
			String url = FILE_SERVER_URL + path;
			System.out.println(url);
			return new Result(true, url);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}

	}

	@RequestMapping("/upload_delete")
	public void upload_delete(String url) {
		//System.out.println(url);
		// http://192.168.25.133/group1/M00/00/00/wKgZhVtpTQiAdAu_AAcsqOIMfgQ544.jpg
		// System.out.println(url.substring(0, 22));
		//System.out.println(url.substring(22, 28));
		//System.out.println(url.substring(29));
		String group = url.substring(22, 28);
		String filePath = url.substring(29);
		DeleteFileUtils.deleteFile(group, filePath);
	}
}
