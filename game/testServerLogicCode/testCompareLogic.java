package com.lbwan.game.testServerLogicCode;

import java.util.ArrayList;
import java.util.List;

import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerComparer.HandPatternCalculator;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.CardTypeEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.spring.SpringUtils;

public class testCompareLogic {
	
    
    public void testComparPorkerLogic(){
    	//GameComparer comparer = new GameComparer();
    	
    	// 测试前面没有牌可以对比的情况
    	boolean bGameStartTag = false;
    	int nPieceNum = 6;
    	int nLastHandPattern = HandPatternCalculator.makeNewHandPattern(CardTypeEnum.CARD_TYPE_BIG_COMMON_BOMB_VALUE, nPieceNum, FaceValueEnum.FACE_VALUE_9_VALUE);
    	//comparer.initTestCodeData(bGameStartTag, nLastHandPattern);
    	
    	// 比较测试
    	PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
    	List<Integer> sumbitPorkerList = new ArrayList<>();
    	
    	/*
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_4_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_5_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	*/
    	
    	
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_3_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	//sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_2_VALUE, CardColorEnum.CARD_COLOR_CLUB));
    	sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	
    	//sumbitPorkerList.add(porkerManager.getPorkerValue(FaceValueEnum.FACE_VALUE_10_VALUE, CardColorEnum.CARD_COLOR_HEART));
    	
    	
    	int nMajorFaceValue = FaceValueEnum.FACE_VALUE_10_VALUE;
    	
    	/*
    	 * boolean bBiggerThanLast = comparer.compareMaxPorker("XiaoLiZi", sumbitPorkerList, nMajorFaceValue);
    	 
    	if(true == bBiggerThanLast){
    		System.out.println("大于");
    	}else{
    		System.out.println("客户端上发错误 ,  不大于");
    	}
    	*/
    	System.out.println("Thanks");
    }
}
