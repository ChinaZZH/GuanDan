package com.lbwan.game.room.roomGameTimer;

import java.util.List;

import org.apache.log4j.Logger;

import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;

public class PayTributeTask implements Runnable{
	
	private RoomGame roomGame;
    
	private Logger logger = Logger.getLogger(this.getClass());
	
	
    public PayTributeTask( RoomGame game ){
        this.roomGame = game;
    }
    
   
    @Override
    public void run() {
    	
    	if(null == this.roomGame){
			 logger.error("PayTributeTask::run() this.roomGame Null Error");
			 return ;
		 }
    	
    	TeamGroup group = this.roomGame.getTeamGroup();
    	if(null == group){
			 logger.error("PayTributeTask::run() group Null Error");
			 return ;
		 }
    	
    	// 获取还没有完成进贡的玩家
    	PayTributeData payTirbute = this.roomGame.getPayTributeData();
    	if(null == payTirbute){
			 logger.error("PayTributeTask::run() payTirbute Null Error");
			 return ;
		 }
    	
    	// 将还没有进贡的玩家系统帮助其完成进贡
    	List<String> notPayTributerList = payTirbute.getPlayerNotCompletePayTribute();
    	int nTributerNum = notPayTributerList.size();
    	for(int i = 0; i < nTributerNum; ++i){
    		String strPayTributeUser = notPayTributerList.get(i);
    		GamePlayer player = group.searchGamePlayerByUserId(strPayTributeUser);
    		if(null == player){
   			   logger.error("PayTributeTask::run() player Null Error");
   			   continue;
   			 }
    		
    		String strBackTributer = payTirbute.getReceivePayTributeUser(strPayTributeUser);
    		GamePlayer backTributer = group.searchGamePlayerByUserId(strBackTributer);
    		if(null == backTributer){
    			logger.error("PayTributeTask::run() backTributer Null Error");
    			continue;
    		}
    		
    		int nFaceValue = payTirbute.getPayTributePorkerFaceValue(strPayTributeUser);
    		int nPayTributePorkerValue = player.getPorkerValueByFaceValue(nFaceValue, group.getCurrentMajorFaceValue());
    		if(0 == nPayTributePorkerValue){
    			 logger.error("PayTributeTask::run() nPayTributePorkerValue 0 Error");
     			 continue;
    		}
    		
    		
    		this.roomGame.payTributePorkerValue(strPayTributeUser, nPayTributePorkerValue);
    	}
    }
}
