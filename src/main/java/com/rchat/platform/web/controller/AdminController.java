package com.rchat.platform.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理的控制器，主要用来对系统资源进行管理
 *
 * @author dzhang
 * @since 2017-02-24 15:04:51
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
	@GetMapping
	public String index() {
		return "admin/index";
	}
}
