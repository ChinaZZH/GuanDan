package com.lbwan.game.cardTypeSearch;


import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.lbwan.game.cardTypeChecker.Checker;
import com.lbwan.game.cardTypeChecker.CommonBombChecker;
import com.lbwan.game.cardTypeChecker.CommonFlushChecker;
import com.lbwan.game.cardTypeChecker.CouplePorkerChecker;
import com.lbwan.game.cardTypeChecker.FlyPairsChecker;
import com.lbwan.game.cardTypeChecker.KingBombChecker;
import com.lbwan.game.cardTypeChecker.SameColorFlushChecker;
import com.lbwan.game.cardTypeChecker.SinglePorkerChecker;
import com.lbwan.game.cardTypeChecker.ThreeBringTwoChecker;
import com.lbwan.game.cardTypeChecker.ThreeFlushChecker;
import com.lbwan.game.cardTypeChecker.ThreePieceChecker;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;



@Service
public class CardTypeCheckerMgr {
	private Map<Integer, Checker> allCheckerMap = new HashMap<Integer, Checker>();

	private Logger logger = Logger.getLogger(getClass());
	
	
	private synchronized void init(){
		if(false == allCheckerMap.isEmpty()){
			return ;
		}
		
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_ONE_PIECE_VALUE, new SinglePorkerChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_ONE_COUPLE_VALUE, new CouplePorkerChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_THREE_PIECE_VALUE, new ThreePieceChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_COMMON_FLUSH_VALUE, new CommonFlushChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE, new ThreeBringTwoChecker());
		
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_FLY_OF_THERE_PAIR, new FlyPairsChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_TWO_CONTINUE_THREE_PIECES_VALUE, new ThreeFlushChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_SMALL_COMMON_BOMB_VALUE, new CommonBombChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_SAME_COLOR_FLUSH_VALUE, new SameColorFlushChecker());
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, new CommonBombChecker());
		
		allCheckerMap.put(CardTypeEnum.CARD_TYPE_KING_OF_BOMB_VALUE, new KingBombChecker());
	}
	
	
	public Checker getSpecficCheckerByValue(int nCheckerValue){
		if(true == allCheckerMap.isEmpty()){
			this.init();
		}
		
		Checker checker = allCheckerMap.get(nCheckerValue);
		if(null == checker){
			logger.error("CardTypeCheckerMgr::getSpecficCheckerByValue checker Null error");
			return null;
		}
		
		return checker;
	}
	
	
}

