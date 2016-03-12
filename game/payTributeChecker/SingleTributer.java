package com.lbwan.game.payTributeChecker;


import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.room.gameTeam.TeamGroup;
import com.lbwan.game.room.roomGame.GamePlayer;

// 单下校验器
public class SingleTributer extends AbstarctTributeChecker{
	public int checkIsBelongToSpecficTribute(TeamGroup teamGroup){
		int nCheckResult = 0;
		if(null == teamGroup){
			logger.error("SingleReactTributer::checkIsBelongToSpecficTribute teamGroup null Error");
			return nCheckResult;
		}
		
		GamePlayer lastPlayer = teamGroup.getLastOutPorkerUser();
		if(null == lastPlayer){
			logger.error("SingleReactTributer::checkIsBelongToSpecficTribute lastPlayer null Error");
			return nCheckResult;
		}
		

		// 表示不能抗贡 需要进贡
		nCheckResult = PayTributeEnum.TRIBUTE_SINGLE_LOW_TRIBUTE;
				
		// 下游者有两根大王可以抗贡
		int nBigKingPorkerValue = porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_BIG_JORKER, CardColorEnum.CARD_COLOR_HEART);
		int nBigKingPorkerNum = lastPlayer.calcuPorkerNumByPorkerValue(nBigKingPorkerValue);
		if(2 == nBigKingPorkerNum){
			nCheckResult = PayTributeEnum.TRIBUTE_SINGLE_LOW_REACT;
		}
		
		return nCheckResult;
	}
}
