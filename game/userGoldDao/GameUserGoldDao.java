package com.lbwan.game.userGoldDao;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.lbwan.game.core.redis.RedisBaseDao;

@Repository
public class GameUserGoldDao extends RedisBaseDao{
	
	private Map<String, Integer>  gameUserGoldMap = null;
	
	private static final String USER_GOLD = "userId:Gold";
	
	public void initLastLoginData(Map<String, Integer>  userGoldMap){
		/*
		this.gameUserGoldMap = userGoldMap;
		hmset(USER_GOLD, userGoldMap);
		*/
	}
	
	public void setUserGoldByUserId(String strUserId, Integer nGold){
		String strGold = nGold.toString();
		hset(USER_GOLD, strUserId, strGold);
	}
	
	public int getUserGoldFromRedis(String strUserId){
		String strGold = hget(USER_GOLD, strUserId);
		if(null == strGold){
			return 0;
		}
		
		Integer nGold = Integer.valueOf(strGold).intValue();
		return nGold;
	}
}
