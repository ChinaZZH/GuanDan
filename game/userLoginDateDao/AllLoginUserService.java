package com.lbwan.game.userLoginDateDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbwan.game.redisTest.LoginDao;
import com.lbwan.game.redisTest.User;
import com.lbwan.game.redisTest.UserStateEnum;
import com.lbwan.game.service.BaseService;

@Service
public class AllLoginUserService extends BaseService{
	 
	 @Autowired
	 private AllLoginUserDao allUserDao;     // 所有已经登陆过的玩家存储在这边
	    
	 @PostConstruct
	 void init(){
	    Map<String, String>  userLastLoginDate = new HashMap<String, String>();
	    
	    
	    allUserDao.initLastLoginData(userLastLoginDate);
	 }
	 
	 public boolean isFirstLogin(String strUserId){
		 String strLastLoginDate = this.getUserLoginTime(strUserId);
		 if(null == strLastLoginDate){
			 return true;
		 }
		 
		 return false;
	 }
	 
	 
	public boolean isTodayFirstLogin(String strUserId){
		 String strLastLoginDate = this.getUserLoginTime(strUserId);
		 if(null == strLastLoginDate){
			 return false;
		 }
		  
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 Date saveDate = null;
		 try{
			 saveDate = dateFormat.parse(strLastLoginDate);
	    	}catch(Exception ex){
	    		return false;
	    }
		 
		 
		 Calendar calSaveDate = Calendar.getInstance();
		 calSaveDate.setTime(saveDate);
		 
		 Date currentDate = new Date();
		 Calendar calCurrentDate = Calendar.getInstance();
		 calCurrentDate.setTime(currentDate);
		 
		 boolean bIsSameYear = calSaveDate.get(Calendar.YEAR) == calCurrentDate.get(Calendar.YEAR);
		 boolean bIsSameMonth = calSaveDate.get(Calendar.MONTH) == calCurrentDate.get(Calendar.MONTH);
		 boolean bIsSameDay = calSaveDate.get(Calendar.DATE) == calCurrentDate.get(Calendar.DATE);
		 if((true == bIsSameYear) && (true == bIsSameMonth) && (true == bIsSameDay)){
			 return false;
		 }
		 
		 return true;
	 }
	 
	 
	 private String getUserLoginTime(String strUserId){
		 String strLastLoginDate = allUserDao.userLastLoginTime(strUserId);
	     return strLastLoginDate;
	 }
	 
	 public void updateUserNewLogin(String strUserId){
		 allUserDao.saveUserTodayLogin(strUserId);
	 }
	
}

