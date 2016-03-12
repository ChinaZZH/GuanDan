package com.lbwan.game.userLoginDateDao;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.lbwan.game.core.redis.RedisBaseDao;
import com.lbwan.game.redisTest.User;

@Repository
public class AllLoginUserDao extends RedisBaseDao{
	
	private Map<String, String>  userLastLoginDate = null;
	
	private static final String ALL_LOGIN_USER = "allUserLogin:Times";
	
	public void initLastLoginData(Map<String, String>  lastLoginDateMap){
		/*
		this.userLastLoginDate = lastLoginDateMap;
		hmset(ALL_LOGIN_USER, userLastLoginDate);
		*/
	}
	
	// 是否存在
	public String userLastLoginTime(String strUserId){
		String lastLoginData = hget(ALL_LOGIN_USER, strUserId);
		if(null == lastLoginData){
			Date currentDate = new Date();
			hset(ALL_LOGIN_USER, strUserId, currentDate.toString());
		}
		
		return lastLoginData;
	}
	
	// sadd
	public void saveUserTodayLogin(String strUserId){
		Date currentDate = new Date();
		hset(ALL_LOGIN_USER, strUserId, currentDate.toString());
    }
}

