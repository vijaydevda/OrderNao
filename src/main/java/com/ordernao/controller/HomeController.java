package com.ordernao.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

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


}
