package com.lbwan.game.payTributeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.RoomGame;

@Service
public class PayTributeCheckerHolder {
	private Map<Integer, TributeChecker> allTributeCheckerMap = new HashMap<Integer, TributeChecker>();

	private List<Integer> singleTributeList = new ArrayList<>();
	
	private List<Integer> doubleTributeList = new ArrayList<>();
	
	private Logger logger = Logger.getLogger(getClass());
	

	public synchronized void initCheckerHolder(){
		if(false == allTributeCheckerMap.isEmpty()){
			return ;
		}
		
		allTributeCheckerMap.put(PayTributeEnum.SINGLE_TRIBUTER, new SingleTributer());
		allTributeCheckerMap.put(PayTributeEnum.DOUBLE_TRIBUTER, new DoubleTributer());
		
		singleTributeList.add(PayTributeEnum.SINGLE_TRIBUTER);
		doubleTributeList.add(PayTributeEnum.DOUBLE_TRIBUTER);
	}
	
	private TributeChecker getTributeCheckerByType(int nCheckerType){
		if(true == allTributeCheckerMap.isEmpty()){
			this.initCheckerHolder();
		}
		
		TributeChecker checker = allTributeCheckerMap.get(nCheckerType);
		return checker;
	}
	
	public int checkPayTributeType(RoomGame currentRoomGame){
		if(true == allTributeCheckerMap.isEmpty()){
			this.initCheckerHolder();
		}
		
		int nErrorType = PayTributeEnum.TRIBUTE_NULL_ERROR;
		if(null == currentRoomGame){
			logger.error("PayTributeCheckerHolder::checkPayTributeType currentRoomGame Null Error");
			return nErrorType;
		}
		
		TeamGroup teamGroupOfGame = currentRoomGame.getTeamGroup();
		if(null == teamGroupOfGame){
			logger.error("PayTributeCheckerHolder::checkPayTributeType teamGroupOfGame Null Error");
			return nErrorType;
		}
		
		Team failTeam = teamGroupOfGame.getTeamOfFailGame();
		if(null == failTeam){
			logger.error("PayTributeCheckerHolder::checkPayTributeType failTeam Null Error");
			return nErrorType;
		}
		
		List<Integer> tributeCheckerList = singleTributeList;
		boolean bIsDoubleLow = failTeam.isDoubleLow();
		if(true == bIsDoubleLow){
			tributeCheckerList = doubleTributeList;
		}
		
		int nCheckerListSize = tributeCheckerList.size();
		for(int i = 0; i < nCheckerListSize; ++i){
			int nTributerType = tributeCheckerList.get(i);
			TributeChecker tributer = this.getTributeCheckerByType(nTributerType);
			if(null == tributer){
				logger.error("PayTributeCheckerHolder::checkPayTributeType nTributerType No Exist Error");
				continue;
			}
			
			int nTributerTypeNo = tributer.checkIsBelongToSpecficTribute(teamGroupOfGame);
			if(0 != nTributerTypeNo){
				return nTributerTypeNo;
			}
		}
		
		return nErrorType;
	}
	
}
