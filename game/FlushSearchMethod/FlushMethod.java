package com.lbwan.game.FlushSearchMethod;

import java.util.List;

import com.lbwan.game.cardTypeSearch.ParameterUtils;
import com.lbwan.game.cardTypeSearch.SearchHelperHolder;
import com.lbwan.game.porkerEnumSet.CardColorEnum;

public class FlushMethod extends AbstractFlushSearch{
	@Override
	protected int getPorkerTotalNum(SearchHelperHolder sameColorFlush,
			ParameterUtils parameter) {
		// TODO Auto-generated method stub
		int nErrorResult = 0;
		if(null == sameColorFlush){
			logger.error("FlushMethod::getPorkerTotalNum sameColorFlush null Error");
			return nErrorResult;
		}
		
		if(null == parameter){
			logger.error("FlushMethod::getPorkerTotalNum parameter null Error");
			return nErrorResult;
		}
		
		if(CardColorEnum.CARD_COLOR_ERROR != parameter.getCardColor()){
			logger.error("FlushMethod::getPorkerTotalNum card Color Error");
			return nErrorResult;
		}
		
		int nPorkerNum = sameColorFlush.getTotalPorkerNum();
		return nPorkerNum;
	}
	
	protected boolean collectPorkerToSumbitContainer(List<Integer> sumbitPorkerList, SearchHelperHolder flushHolder, ParameterUtils parameter, int nAddPorkerToContainer){
		boolean bCollectResult = false;
		if(null == sumbitPorkerList){
			logger.error("FlushMethod::collectPorkerToSumbitContainer sumbitPorkerList null Error");
			return bCollectResult;
		}
		
		if(null == flushHolder){
			logger.error("FlushMethod::collectPorkerToSumbitContainer sameColorFlush null Error");
			return bCollectResult;
		}
		
		if(null == parameter){
			logger.error("FlushMethod::collectPorkerToSumbitContainer parameter null Error");
			return bCollectResult;
		}
		
		if(CardColorEnum.CARD_COLOR_ERROR != parameter.getCardColor()){
			logger.error("FlushMethod::collectPorkerToSumbitContainer CardColor Error");
			return bCollectResult;
		}
		
		flushHolder.collectFaceValueByNum(sumbitPorkerList, nAddPorkerToContainer);
		bCollectResult = true;
		return bCollectResult;
	}
	
	protected boolean isAllTheDiffColorOfPorker(List<Integer> sumbitPorkerList){
		return true;
	}
}
