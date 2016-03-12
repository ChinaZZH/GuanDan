package com.lbwan.game.FlushSearchMethod;

import java.util.List;

import com.lbwan.game.cardTypeSearch.ParameterUtils;
import com.lbwan.game.cardTypeSearch.SearchHelperHolder;
import com.lbwan.game.porkerEnumSet.CardColorEnum;

public class SameColorMethod extends AbstractFlushSearch{

	@Override
	protected int getPorkerTotalNum(SearchHelperHolder sameColorFlush,
			ParameterUtils parameter) {
		// TODO Auto-generated method stub
		int nErrorResult = 0;
		if(null == sameColorFlush){
			logger.error("SameColorMethod::getPorkerTotalNum sameColorFlush null Error");
			return nErrorResult;
		}
		
		if(null == parameter){
			logger.error("SameColorMethod::getPorkerTotalNum parameter null Error");
			return nErrorResult;
		}
		
		if(CardColorEnum.CARD_COLOR_ERROR == parameter.getCardColor()){
			logger.error("SameColorMethod::getPorkerTotalNum card Color Error");
			return nErrorResult;
		}
		
		int nPorkerNum = sameColorFlush.getPorkerNumByColor(parameter.getCardColor());
		return nPorkerNum;
	}

	protected boolean collectPorkerToSumbitContainer(List<Integer> sumbitPorkerList, SearchHelperHolder flushHolder, ParameterUtils parameter, int nAddPorkerToContainer){
		boolean bCollectResult = false;
		if(null == sumbitPorkerList){
			logger.error("SameColorMethod::collectPorkerToSumbitContainer sumbitPorkerList null Error");
			return bCollectResult;
		}
		
		if(null == flushHolder){
			logger.error("SameColorMethod::collectPorkerToSumbitContainer sameColorFlush null Error");
			return bCollectResult;
		}
		
		if(null == parameter){
			logger.error("SameColorMethod::collectPorkerToSumbitContainer parameter null Error");
			return bCollectResult;
		}
		
		if(CardColorEnum.CARD_COLOR_ERROR == parameter.getCardColor()){
			logger.error("SameColorMethod::collectPorkerToSumbitContainer CardColor Error");
			return bCollectResult;
		}
		
		flushHolder.putSearcherPorkerByPieces(sumbitPorkerList, nAddPorkerToContainer, parameter.getCardColor());
		bCollectResult = true;
		return bCollectResult;
	}
	
	protected boolean isAllTheDiffColorOfPorker(List<Integer> sumbitPorkerList){
		return true;
	}
}
