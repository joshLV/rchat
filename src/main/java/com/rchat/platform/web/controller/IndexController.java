package com.rchat.platform.web.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.rchat.platform.common.RchatEnv;
import com.rchat.platform.common.RchatUtils;

@Controller
@RequestMapping
public class IndexController {
	@Autowired
	private RchatEnv env;

	/**
	 * 如果想利用 controller 呈现页面，切直接返回的是页面名称的话，名字不能跟请求的路径相同。 如果加入了其他的 模版引擎，就随意了
	 * 
	 * @return 登录页面或者主页
	 */
	@RequestMapping("/login")
	public String login(HttpSession session) {
		Object securityUser = session.getAttribute(RchatUtils.SECURITY_USER);
		if (securityUser != null) {
			return "redirect:/";
		}
		return "login";
	}

	@PostMapping("/login")
	public ModelAndView login(String username, String password) {
		return new ModelAndView("index");
	}

	@GetMapping
	public String index(Model model, HttpSession session) {
		session.setAttribute(RchatUtils.SECURITY_USER, RchatUtils.currentUser());
		if (RchatUtils.isRchatAdmin()) {
			model.addAttribute("resource", env.currentAgent());
			return "rchat";
		} else if (RchatUtils.isAgentAdmin()) {
			model.addAttribute("resource", env.currentAgent());
			return "agent";
		} else if (RchatUtils.isGroupAdmin()) {
			model.addAttribute("resource", env.currentGroup());
			return "group";
		} else if (RchatUtils.isDepartmentAdmin()) {
			model.addAttribute("resource", env.currentDepartment());
			return "department";
		} else {
			return "index";
		}
	}
}
