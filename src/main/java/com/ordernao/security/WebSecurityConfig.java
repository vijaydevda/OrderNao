package com.ordernao.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	/*
	 * http.authorizeRequests().antMatchers("/login").permitAll().anyRequest
	 * ().authenticated().and().formLogin()
	 * .loginPage("/login").permitAll().and().logout().permitAll().logoutUrl
	 * ("/logout").permitAll().logoutSuccessUrl("/login?logout").and().csrf(
	 * ).disable();
	 */
	http.authorizeRequests().antMatchers("/login").permitAll().anyRequest().authenticated().and().formLogin()
		.loginPage("/login").usernameParameter("username").passwordParameter("password").permitAll().and()
		.logout().permitAll().logoutUrl("/logout").permitAll().logoutSuccessUrl("/login?logout").and().csrf()
		.disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	/*
	 * auth .inMemoryAuthentication()
	 * .withUser("user@gmail.com").password("password").roles("USER");
	 */
	auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery("select email as username,password,true as enabled from users where email = ?")
		.authoritiesByUsernameQuery(
			"select email as username,role_name as role from users inner join roles on users.roleid = roles.role_id where email = ?");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
	web.ignoring().antMatchers("/resources/**", "/js/**", "/css/**", "/image/**"); // #3
    }
}