package com.lbwan.game.room.roomGameTimer;

import java.util.List;

import org.apache.log4j.Logger;

import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.payTributeData.PayTributeData;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;

public class BackTributeTask implements Runnable{
	
	private RoomGame roomGame;
	
	//private String backTributeUser;
    
	private Logger logger = Logger.getLogger(this.getClass());
	
	
    public BackTributeTask( RoomGame game ){
        this.roomGame = game;
        //this.backTributeUser = strBackTributeUser;
    }
    
   
    @Override
    public void run() {
    	
    	if(null == this.roomGame){
			 logger.error("BackTributeTask::run() this.roomGame Null Error");
			 return ;
		}
   	
    	TeamGroup group = this.roomGame.getTeamGroup();
       	if(null == group){
       		logger.error("BackTributeTask::run() group Null Error");
			 return ;
    	}
   	
   	
       	// 获取还没有完成进贡的玩家
       	PayTributeData payTirbute = this.roomGame.getPayTributeData();
       	if(null == payTirbute){
			 logger.error("BackTributeTask::run() payTirbute Null Error");
			 return ;
		}
       	       
        // 将还没有进贡的玩家系统帮助其完成进贡
       	List<String> notBackTributerList = payTirbute.getPlayerNotCompleteBackTribute();
       	int nTributerNum = notBackTributerList.size();
       	for(int i = 0; i < nTributerNum; ++i){
       		String strBackTributeUser = notBackTributerList.get(i);
       		GamePlayer backPlayer = group.searchGamePlayerByUserId(strBackTributeUser);
       		if(null == backPlayer){
       			 logger.error("BackTributeTask::run() strBackTributeUser Null Error");
       			 continue;
       		}
       		
       		String strPayTributer = payTirbute.getPayTributeUserByBackTributer(strBackTributeUser);
       		
       		// 进贡方
       		GamePlayer payTributer = group.searchGamePlayerByUserId(strPayTributer);
       		if(null == payTributer){
       			logger.error("BackTributeTask::run() payTributer Null Error");
       			continue;
       		}
       		
       		List<Integer> backPorkerValueList = backPlayer.getMustSumbitMinPorker(group.getCurrentMajorFaceValue());
       		if(true == backPorkerValueList.isEmpty()){
           		logger.error("BackTributeTask::run() backPorkerValueList.isEmpty() True Error");
    			return ;
           	}
       		
       		
       		this.roomGame.backTributePorkerValue(backPlayer.getGamePlayerId(), backPorkerValueList.get(0));
       	}
       	
    }
}




