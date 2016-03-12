package com.lbwan.game.payTributeHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.WhippedEgg.SServerNotifyPayTribute;
import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;
import com.lbwan.game.room.roomGame.RoomGame;
import com.lbwan.game.spring.SpringUtils;
import com.lbwan.game.utils.GDPropertiesUtils;

public class AbstractPayTributeHandler implements PayTributeHandler{
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	protected PayTributeHandlerHolder payTributeHandlerHolder = (PayTributeHandlerHolder) SpringUtils.getBeanByName("payTributeHandlerHolder");
	
	public boolean processPayTribute(RoomGame currentRoomGame){
		return false;
	}
	
	// first 为排名, second 为姓名
	protected Map<Integer, String>  getRankNumberAndUserIdMap(Team winnerTeam){
		if(null == winnerTeam){
			logger.error("AbstractPayTributeHandler::getRankNumberAndUserIdMap failerTeam Null Error");
			return null;
		}
		
		Map<String, GamePlayer> allTeamMember = winnerTeam.getAllTeamMembers();
		if(null == allTeamMember){
			logger.error("AbstractPayTributeHandler::getRankNumberAndUserIdMap allTeamMember Null Error");
			return null;
		}
		
		Map<Integer, String> rankNumberPlayerMap = new HashMap<Integer, String>();
		Iterator<Map.Entry<String, GamePlayer>> iter = allTeamMember.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("AbstractPayTributeHandler::getTributePlayerMap player Null Error");
				continue;
			}
			
			rankNumberPlayerMap.put(player.getOutAllPorkerRank(), player.getGamePlayerId());
		}
		
		return rankNumberPlayerMap;
	}
	
	protected boolean recordPayTributeAndNotifyClient(List<GamePlayer> playerList, TeamGroup currentTeamGroup){
		boolean bRecordResult = false;
		if(null == currentTeamGroup){
			logger.error("AbstractPayTributeHandler::recordPayTributeAndNotifyClient currentTeamGroup Null Error");
			return bRecordResult;
		}
		
		if(null == playerList){
			logger.error("AbstractPayTributeHandler::recordPayTributeAndNotifyClient playerList Null Error");
			return bRecordResult;
		}
		
		int nPlayerListNum = playerList.size();
		/*
		for(int i = 0; i < nPlayerListNum; ++i){
			GamePlayer player = playerList.get(i);
			if(null == player){
				logger.error("AbstractPayTributeHandler::recordPayTributeAndNotifyClient player Null Error");
				continue;
			}
			
			player.processPayTribute();
		}
		*/
		
		int nPayTributeTimeSec = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.PAY_TRIBUTE_TIMER);
		int nSecsDiff = GDPropertiesUtils.getPropertyAsInteger(GDPropertiesUtils.SERVER_CLIENTSECS, 2);
		
		SServerNotifyPayTribute.Builder payTributeBuilder = SServerNotifyPayTribute.newBuilder();
		for(int i = 0; i < nPlayerListNum; ++i){
			GamePlayer player = playerList.get(i);
			if(null == player){
				logger.error("AbstractPayTributeHandler::recordPayTributeAndNotifyClient player Null Error");
				continue;
			}
			
			payTributeBuilder.addStrPayTributerId(player.getGamePlayerId());
		}
		
		payTributeBuilder.setNPayTributeNeedSecs(nPayTributeTimeSec-nSecsDiff);
		currentTeamGroup.nofityAllOnLineUser(CmdType.CMD_SERVER_NOTIFY_PAY_TRIBUTE_VALUE, payTributeBuilder.build().toByteArray());
		
		bRecordResult = true;
		return bRecordResult;
	}
	
}
