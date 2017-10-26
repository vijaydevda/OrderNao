package com.ordernao.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int authenticateUser(String userName, String password) {
		String sql = "SELECT COUNT(*) from  users where email = ? AND password = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { userName, password }, Integer.class);
	}
}

