package com.lbwan.game.userGoldDao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lbwan.game.service.BaseService;

@Service
public class GameUserGoldService extends BaseService{
	@Autowired
	 private GameUserGoldDao userGoldDao;     // 所有已经登陆过的玩家存储在这边
	    
	 @PostConstruct
	 void init(){
	    Map<String, Integer>  userGoldMap = new HashMap<String, Integer>();
	    userGoldDao.initLastLoginData(userGoldMap);
	 }
	 
	 public void setUserGoldByUserId(String strUserId, Integer nGold){
		 userGoldDao.setUserGoldByUserId(strUserId, nGold);
	 }
	 
	 public int getUserGoldFromRedis(String strUserId){
		 int nUserGold = userGoldDao.getUserGoldFromRedis(strUserId);
		 return nUserGold;
	 }
}
