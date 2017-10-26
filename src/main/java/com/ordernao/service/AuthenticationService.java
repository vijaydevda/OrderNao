package com.ordernao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ordernao.dao.AuthenticationDao;
@Component
public class AuthenticationService {
	@Autowired
	private AuthenticationDao authDao;
	public int authenticateUser(String userName, String password) {
		return authDao.authenticateUser(userName, password);
	}

}
