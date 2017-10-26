package com.ordernao.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ordernao.service.AuthenticationService;

@Controller
public class HomeController {
	@Autowired
	private AuthenticationService authService;

	@RequestMapping(value = "/login.html")
	public String showlogin(HttpServletRequest request, HttpServletResponse response) {
		return "login";
	}

	@RequestMapping(value = "/homepage.html")
	public String showHome(HttpServletRequest request, HttpServletResponse response) {
		return "homepage";
	}

	@RequestMapping(value = "/addemploy.html")
	public String addEmployee(HttpServletRequest request, HttpServletResponse response) {
		return "addemploy";
	}

	@ResponseBody
	@RequestMapping(value = "/authenticateUser.html")
	public String authenticateUser(@RequestParam("LoginEmail") String userName,
			@RequestParam("LoginPassword") String password) {
		String result = "Failed";
		int authValue = authService.authenticateUser(userName, password);
		if (authValue > 0) {
			result = "Success";
		}
		return result;
	}
}
