package com.rchat.platform.web.controller.api;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rchat.platform.common.JavaShellUtil;
import com.rchat.platform.common.LogAPI;

@RestController
@RequestMapping("/api/shells")
public class JaveShellController {
	@Autowired
	private JavaShellUtil util;
	
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	@LogAPI("上传导入数据库")
	public boolean useShell(@RequestParam(value="file", required=true) MultipartFile file){
		try {
			int export = 0;
			//上传SQL
			//分别获取的是变量名file---文件类型---文件名
	        System.out.println(file.getName()+"---"+file.getContentType()+"---"+file.getOriginalFilename());
            if (!file.isEmpty()){
            //使用StreamsAPI方式拷贝文件
                Streams.copy(file.getInputStream(),new FileOutputStream("/mnt/export/rchat.sql"),true);
            }
			//执行备份导出SHELL
			export = util.executeShell("sh /usr/upload.sh");
			if(export == 1){
				return true;
			} else {
				return false;
			}			
		} catch (IOException e) {
			System.out.println("文件上传失败");
			e.printStackTrace();
			return false;
		}
	}
	
	@PostMapping("/download")
	@LogAPI("备份数据库")
	public boolean download(HttpServletRequest request, HttpServletResponse response){
		try {
			int export = 0;
			//执行备份导出SHELL
			export = util.executeShell("sh /usr/download.sh");
			if(export == 1){
				return true;
			} else {
				return false;
			}		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@PostMapping("/console")
	@LogAPI("下载日志")
	public boolean console(HttpServletRequest request, HttpServletResponse response){
		try {
			int export = 0;
			//执行备份导出SHELL
			export = util.executeShell("sh /usr/console.sh");
			if(export == 1){
				return true;
			} else {
				return false;
			}		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
