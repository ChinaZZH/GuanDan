package com.lbwan.game.testServerLogicCode;

import java.util.ArrayList;
import java.util.List;

import com.lbwan.game.cardTypeChecker.Checker;
import com.lbwan.game.cardTypeSearch.CardTypeCheckerMgr;
import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.spring.SpringUtils;

public class TestCardTypeLogic {
	
	public TestCardTypeLogic(){
		
	}
	
	
	public void testCardTypeLogic(){
    	CardTypeCheckerMgr checkerManager = (CardTypeCheckerMgr) SpringUtils.getBeanByName("cardTypeCheckerMgr");
    	Checker checker = checkerManager.getSpecficCheckerByValue(CardTypeEnum.CARD_TYPE_THREE_BRING_TWO_VALUE);
    	
    	// 测试三根
    	PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
    	List<Integer> sumbitPorkerList = new ArrayList<>();
    	
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_Q_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_Q_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	
    	
    	int nCardType = checker.isBelongToSpecficType(sumbitPorkerList, FaceValueEnum.FACE_VALUE_10_VALUE);
    	System.out.println("CardType: " + nCardType);
    	System.out.println("Thanks");
    }
	
	
	private List<Integer> makeHandPorker(){	
    	List<Integer> sumbitPorkerList = new ArrayList<>();
    	PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_4_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_5_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_6_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_7_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_J_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	return sumbitPorkerList;
    }
}
