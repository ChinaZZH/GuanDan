package com.lbwan.game.payTributeChecker;

import java.util.Iterator;
import java.util.Map;

import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.room.gameTeam.Team;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;

public class DoubleTributer extends AbstarctTributeChecker{
	public int checkIsBelongToSpecficTribute(TeamGroup teamGroup){
		int nCheckResult = 0;
		if(null == teamGroup){
			logger.error("DoubleTributer::checkIsBelongToSpecficTribute teamGroup null Error");
			return nCheckResult;
		}
		
		Team failTeam = teamGroup.getTeamOfFailGame();
		if(null == failTeam){
			logger.error("DoubleTributer::checkIsBelongToSpecficTribute failTeam null Error");
			return nCheckResult;
		}
		
		// 下游者有两根大王可以抗贡
		int nBigKingPorkerValue = porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_BIG_JORKER, CardColorEnum.CARD_COLOR_HEART);
		int nBigKingPorkerNum = this.calcuPorkerNumByPorkerValueOfTeam(failTeam, nBigKingPorkerValue);
		
		
		// 双下   可以抗贡
		if(2 == nBigKingPorkerNum){
			nCheckResult = PayTributeEnum.TRIBUTE_ALL_LOW_REACT;
			return  nCheckResult;
		}
		
		// 两根一样大 
		boolean bSameOfPorker = this.isSameBiggestPorkerWithTeam(failTeam, teamGroup.getCurrentMajorFaceValue());
		if(true == bSameOfPorker){
			nCheckResult = PayTributeEnum.TRIBUTE_ALL_LOW_SAME_TRIBUTE_PORKER;
			return  nCheckResult;
		}
		
		// 两者最大的牌不一样大
		nCheckResult = PayTributeEnum.TRIBUTE_ALL_LOW_DIFF_TRIBUTE_PORKER;
		return nCheckResult;
	}
	
	// 计算队伍中 有该牌的总量是多少
	private int calcuPorkerNumByPorkerValueOfTeam(Team failTeam, int nBigKingPorkerValue){
		int nBigKingPorkerNum = 0;
		if(null == failTeam){
			logger.error("DoubleTributer::calcuPorkerNumByPorkerValueOfTeam failTeam null Error");
			return nBigKingPorkerNum;
		}
		
		// 访问容器计算
		Map<String, GamePlayer> playerMap = failTeam.getAllTeamMembers();
		Iterator<Map.Entry<String, GamePlayer>> iter = playerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("DoubleTributer::calcuPorkerNumByPorkerValueOfTeam player null Error");
				continue;
			}
			
			nBigKingPorkerNum = nBigKingPorkerNum + player.calcuPorkerNumByPorkerValue(nBigKingPorkerValue);
		}
		
		return nBigKingPorkerNum;
	}
	
	// 队伍中  所有人最大牌(逢人配 不算在内)
	private boolean isSameBiggestPorkerWithTeam(Team failTeam, int nMajorFace){
		boolean bSamePorkerResult = false;
		if(null == failTeam){
			logger.error("DoubleTributer::isSameBiggestPorkerWithTeam failTeam null Error");
			return bSamePorkerResult;
		}
		
		int nMajorHeartPorkerValue = FaceValueEnum.getSpecficHeartByFaceValue(nMajorFace);
		int nMaxPorkerFace = 0;
		
		// 访问容器计算
		Map<String, GamePlayer> playerMap = failTeam.getAllTeamMembers();
		Iterator<Map.Entry<String, GamePlayer>> iter = playerMap.entrySet().iterator();
		while(true == iter.hasNext()){
			Map.Entry<String, GamePlayer> entry = iter.next();
			GamePlayer player = entry.getValue();
			if(null == player){
				logger.error("DoubleTributer::isSameBiggestPorkerWithTeam player null Error");
				continue;
			}
			
			// 逢人配 是否算在内
			int nTempBiggestPorkerFace = player.getBiggestPorkerFaceExcept(nMajorHeartPorkerValue);
			if(0 == nMaxPorkerFace){
				nMaxPorkerFace = nTempBiggestPorkerFace;
				continue;
			}
			
			if(nMaxPorkerFace != nTempBiggestPorkerFace){
				return bSamePorkerResult;
			}
		}
		
		// 异常
		if(0 == nMaxPorkerFace){
			logger.error("DoubleTributer::isSameBiggestPorkerWithTeam Server Logic Error");
			return bSamePorkerResult;
		}
		
		bSamePorkerResult = true;
		return true;
	}
}


