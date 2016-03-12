package com.lbwan.game.porker;

import com.lbwan.game.porkerEnumSet.CardColorEnum;
import com.lbwan.game.porkerEnumSet.FaceValueEnum;
import com.lbwan.game.porkerEnumSet.PorkerValueEnum;

public class Porker {
	private int  porkerEnumValue = 0;
	private int faceValue= FaceValueEnum.FACE_VALUE_NULL;
	private int color = CardColorEnum.CARD_COLOR_NULL;
	
	public Porker(int nPorkerEnumValue){
		this.porkerEnumValue = nPorkerEnumValue;
		this.faceValue = PorkerValueEnum.getFaceValueByPorkerValueOf(nPorkerEnumValue);
		this.color = PorkerValueEnum.getCardColorByPorkerValue(nPorkerEnumValue);
	}
	
	public int getFaceValue(){
		return faceValue;
	}
	
	public int getColor(){
		return color;
	}
	
	public int getPorkerEnumValue(){
		return porkerEnumValue;
	}
}
