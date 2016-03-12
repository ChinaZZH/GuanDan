package com.lbwan.game.FlushSearchMethod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lbwan.game.cardTypeSearch.ParameterUtils;
import com.lbwan.game.cardTypeSearch.SearchHelperHolder;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;

public class DiffColorMethod extends FlushMethod{

	protected boolean isAllTheDiffColorOfPorker(List<Integer> sumbitPorkerList){
		//如果是同花 则排除继续查找
		boolean bErrorResult = false;
		if(null == sumbitPorkerList){
			logger.error("DiffColorMethod::isAllTheDiffColorOfPorker sumbitPorkerList null Error ");
			return bErrorResult;
		}
		
		if(true == sumbitPorkerList.isEmpty()){
			logger.error("DiffColorMethod::isAllTheDiffColorOfPorker sumbitPorkerList empty ");
			return bErrorResult;
		}
		
		// 如果是同花  则不是普通的顺子
		int nFristIndex = 0;
		int nColor = PorkerValueEnum.getCardColorByPorkerValue(sumbitPorkerList.get(nFristIndex));
		int nSumbitSize = sumbitPorkerList.size();
		for(int n = 1; n < nSumbitSize; ++n){
			int nTempPorkerValue = sumbitPorkerList.get(n);
			int nCompareColor = PorkerValueEnum.getCardColorByPorkerValue(nTempPorkerValue);
			if(nCompareColor != nColor){
				bErrorResult = true;
				return bErrorResult;
			}
		}
		
		bErrorResult = false;
		return bErrorResult;
	}
}
