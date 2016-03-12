package com.lbwan.game.cardTypeSearch;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lbwan.game.porker.PorkerMgr;
import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.spring.SpringUtils;

public class SearchHelperHolder {
	
	@Autowired
	protected PorkerMgr porkerManager = (PorkerMgr) SpringUtils.getBeanByName("porkerMgr");
	
	protected Logger logger = Logger.getLogger(getClass());
	
	private int  faceValue = 0;        // 面值
	
	private int  heartPorkerNum = 0;   // 该面值红桃的个数
	
	private int  diamondPorkerNum = 0; // 该面值方块的个数
	
	private int  spadePorkerNum = 0;   // 该面值黑桃的个数
	
	private int  clubPorkerNum = 0;    // 该面值草花的个数
	
	private int  specficPorkerNum = 0;   // 该面值方块的个数
	
	public SearchHelperHolder(int nFaceValue){
		this.faceValue = nFaceValue;
		
		this.heartPorkerNum = 0;
		this.diamondPorkerNum = 0;
		this.spadePorkerNum = 0;
		this.clubPorkerNum = 0;
		this.specficPorkerNum = 0;
	}
	
	
	public int getTotalPorkerNum(){
		int nTotalPorkerNum = this.heartPorkerNum + this.diamondPorkerNum + this.spadePorkerNum + this.clubPorkerNum + this.specficPorkerNum;
		return nTotalPorkerNum;
	}
	
	public int getHeartPorkerNum(){
		return this.heartPorkerNum;
	}
	
	public int getDiamondPorkerNum(){
		return this.diamondPorkerNum;
	}
	
	public int getSpadePorkerNum(){
		return this.spadePorkerNum;
	}
	
	public int getClubPorkerNum(){
		return this.clubPorkerNum;
	}
	
	public int getSpecficPorkerNum(){
		return this.specficPorkerNum;
	}
	
	public boolean addFaceValueByColor(int nCardColor){
		boolean bAddNewColor = false;
		if((FaceValueEnum.FACE_VALUE_SMALL_JORKER == this.faceValue) || (FaceValueEnum.FACE_VALUE_BIG_JORKER == this.faceValue)){
			this.addSpecficNum();
			
			bAddNewColor = true;
			return bAddNewColor;
		}
		
		bAddNewColor = true;
		switch(nCardColor){
		case CardColorEnum.CARD_COLOR_HEART:{
			this.addHeartNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_DIAMOND:{
			this.addDiamondNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_SPADE:{
			this.addSpadeNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_CLUB:{
			this.addClubNum();
		}
		break;
		
		default:{
			logger.error("SearchHelperHolder::addFaceValueByColor Color Enum Error: " + nCardColor);
			bAddNewColor = false;
		}
		break;
	  }
		
		
		return bAddNewColor;
    }
	
	private void addHeartNum(){
		this.heartPorkerNum = this.heartPorkerNum + 1;
	}
		
	private void addDiamondNum(){
		this.diamondPorkerNum = this.diamondPorkerNum + 1;
	}
	
	private void addSpadeNum(){
		this.spadePorkerNum = this.spadePorkerNum + 1;
	}
	
	private void addClubNum(){
		this.clubPorkerNum = this.clubPorkerNum + 1;
	}
	
	private void addSpecficNum(){
		this.specficPorkerNum = this.specficPorkerNum + 1;
	}
	
	public void setHeartValueNum(int nHeartValueNum){
		this.heartPorkerNum = nHeartValueNum;
	}
	
	public boolean decHeartValueNum(int nHeartValueEnum){
		this.heartPorkerNum = 0;
		if(this.heartPorkerNum >= nHeartValueEnum){
			this.heartPorkerNum = this.heartPorkerNum - nHeartValueEnum;
		}
		
		return true;
	}
	
	public boolean collectFaceValueByNum(List<Integer> sumbitPorkerList, int nSearcherPieceNum){
		boolean bCollectResult = true;
		boolean bIsKingPorker = ((FaceValueEnum.FACE_VALUE_SMALL_JORKER == this.faceValue) || (FaceValueEnum.FACE_VALUE_BIG_JORKER == this.faceValue));
		// 有两个大王   和  两个小王的情况下
		if(true == bIsKingPorker){
			nSearcherPieceNum = this.putSearcherPorkerByPieces(sumbitPorkerList, nSearcherPieceNum, CardColorEnum.CARD_COLOR_NULL);
			if(0 == nSearcherPieceNum){
				return bCollectResult;
			}
			
			bCollectResult = false;
			return bCollectResult;
		}
		
		
		nSearcherPieceNum = this.putSearcherPorkerByPieces(sumbitPorkerList, nSearcherPieceNum, CardColorEnum.CARD_COLOR_DIAMOND);
		if(0 == nSearcherPieceNum){
			return bCollectResult;
		}
		
		nSearcherPieceNum = this.putSearcherPorkerByPieces(sumbitPorkerList, nSearcherPieceNum, CardColorEnum.CARD_COLOR_SPADE);
		if(0 == nSearcherPieceNum){
			return bCollectResult;
		}
		
		nSearcherPieceNum = this.putSearcherPorkerByPieces(sumbitPorkerList, nSearcherPieceNum, CardColorEnum.CARD_COLOR_CLUB);
		if(0 == nSearcherPieceNum){
			return bCollectResult;
		}
		
		// 考虑到可能是 逢人配, 则红桃放在最后一栏
		nSearcherPieceNum = this.putSearcherPorkerByPieces(sumbitPorkerList, nSearcherPieceNum, CardColorEnum.CARD_COLOR_HEART);
		if(0 == nSearcherPieceNum){
			return bCollectResult;
		}
		
		bCollectResult = false;
		return bCollectResult;
	}
	
	public int putSearcherPorkerByPieces(List<Integer> sumbitPorkerList, int nSearchPieceNum, int nColor){
		int nPieceNumByColor = 0;
		switch(nColor){
		case CardColorEnum.CARD_COLOR_HEART:{
			nPieceNumByColor = this.getHeartPorkerNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_DIAMOND:{
			nPieceNumByColor = this.getDiamondPorkerNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_SPADE:{
			nPieceNumByColor = this.getSpadePorkerNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_CLUB:{
			nPieceNumByColor = this.getClubPorkerNum();
		}
		break;
		
		default:{
			nPieceNumByColor = this.getSpecficPorkerNum();
		}
		break;
		
	   }
		
		int nAddPieceNum = nSearchPieceNum;
		if(nSearchPieceNum > nPieceNumByColor){
			nAddPieceNum = nPieceNumByColor;
		}
		
		int nPorkerValue = porkerManager.getPorkerValue(this.faceValue, nColor);
		for(int i = 0; i < nAddPieceNum; ++i){
			sumbitPorkerList.add(nPorkerValue);
		}
		

		int nResultPieceNum = nSearchPieceNum - nAddPieceNum;
		return nResultPieceNum;
	}
	
	public int getPorkerNumByColor(int nColor){
		switch(nColor){
		case CardColorEnum.CARD_COLOR_HEART:{
			return this.getHeartPorkerNum();
		}
		
		case CardColorEnum.CARD_COLOR_DIAMOND:{
			return this.getDiamondPorkerNum();
		}
	
		case CardColorEnum.CARD_COLOR_SPADE:{
			return this.getSpadePorkerNum();
		}
		
		case CardColorEnum.CARD_COLOR_CLUB:{
			return this.getClubPorkerNum();
		}
		
		default:{
			return this.getSpecficPorkerNum();
		}
	  }
	}
	
	
	public boolean decFaceValueByColor(int nCardColor){
		boolean bAddNewColor = false;
		if((FaceValueEnum.FACE_VALUE_SMALL_JORKER == this.faceValue) || (FaceValueEnum.FACE_VALUE_BIG_JORKER == this.faceValue)){
			this.decSpecficNum();
			
			bAddNewColor = true;
			return bAddNewColor;
		}
		
		bAddNewColor = true;
		switch(nCardColor){
		case CardColorEnum.CARD_COLOR_HEART:{
			this.decHeartNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_DIAMOND:{
			this.decDiamondNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_SPADE:{
			this.decSpadeNum();
		}
		break;
		
		case CardColorEnum.CARD_COLOR_CLUB:{
			this.decClubNum();
		}
		break;
		
		default:{
			logger.error("SearchHelperHolder::decFaceValueByColor Color Enum Error: " + nCardColor);
			bAddNewColor = false;
		}
		break;
	  }
		
		
		return bAddNewColor;
    }
	
	private void decHeartNum(){
		if(this.heartPorkerNum < 1){
			logger.error("SearchHelperHolder::decHeartNum Porker Num: " + this.heartPorkerNum);
			return ;
		}
		
		this.heartPorkerNum = this.heartPorkerNum - 1;
	}
		
	private void decDiamondNum(){
		if(this.diamondPorkerNum < 1){
			logger.error("SearchHelperHolder::decDiamondNum Porker Num: " + this.diamondPorkerNum);
			return ;
		}
		
		this.diamondPorkerNum = this.diamondPorkerNum - 1;
	}
	
	private void decSpadeNum(){
		if(this.spadePorkerNum < 1){
			logger.error("SearchHelperHolder::decSpadeNum Porker Num: " + this.spadePorkerNum);
			return ;
		}
		
		this.spadePorkerNum = this.spadePorkerNum - 1;
	}
	
	private void decClubNum(){
		if(this.clubPorkerNum < 1){
			logger.error("SearchHelperHolder::decClubNum Porker Num: " + this.clubPorkerNum);
			return ;
		}
		
		this.clubPorkerNum = this.clubPorkerNum - 1;
	}
	
	private void decSpecficNum(){
		if(this.specficPorkerNum < 1){
			logger.error("SearchHelperHolder::decSpecficNum Porker Num: " + this.specficPorkerNum);
			return ;
		}
		
		this.specficPorkerNum = this.specficPorkerNum - 1;
	}

}


