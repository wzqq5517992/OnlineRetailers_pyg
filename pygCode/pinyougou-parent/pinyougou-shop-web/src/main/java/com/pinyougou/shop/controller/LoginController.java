package com.pinyougou.shop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * 显示登录名
 * @author Jolin
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	@RequestMapping("/name")
	public Map name(){
		String name=SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("用户名："+name);
		Map map=new HashMap();
		map.put("loginName", name);

		return map;
		
	}
}
