package com.lbwan.game.payTributeChecker;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.spring.SpringUtils;

public class AbstarctTributeChecker implements TributeChecker{
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	public int checkIsBelongToSpecficTribute(TeamGroup teamGroup){
		return 0;
	}
}
