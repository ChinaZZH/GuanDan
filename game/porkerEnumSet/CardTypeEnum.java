package com.lbwan.game.porkerEnumSet;

public enum CardTypeEnum {
	CardTypeEnum_Null(0, "空"),
	CardTypeEnum_OnePiece(1, "单跟"),
	CardTypeEnum_OneCouple(2, "一对"),
	CardTypeEnum_ThreePiece(3, "三跟"),
	CardTypeEnum_CommonFlush(4, "顺子"),
	
	CardTypeEnum_ThreeBringTwo(5, "三带二"),
	CardTypeEnum_FlyOfThreePair(6, "三连对"),
	CardTypeEnum_TwoContinueThreePiece(7, "三顺"),
	
	CardTypeEnum_SmallCommonOfBomb(8, "4-5根普通炸"),
	CardTypeEnum_SameColorFlush(9, "同花顺"),
	CardTypeEnum_BigCommonOfBomb(10, "6-10普通炸"),
	CardTypeEnum_KingOfBomb(11, "王炸"),
	;
	
	public static final int CARD_TYPE_NULL_VALUE = 0;
	public static final int CARD_TYPE_ONE_PIECE_VALUE = 1;
	public static final int CARD_TYPE_ONE_COUPLE_VALUE = 2;
	public static final int CARD_TYPE_THREE_PIECE_VALUE = 3;
	public static final int CARD_TYPE_COMMON_FLUSH_VALUE = 4;
	
	public static final int CARD_TYPE_THREE_BRING_TWO_VALUE = 5;
	public static final int CARD_TYPE_FLY_OF_THERE_PAIR = 6;
	public static final int CARD_TYPE_TWO_CONTINUE_THREE_PIECES_VALUE = 7;
	
	public static final int CARD_TYPE_SMALL_COMMON_BOMB_VALUE = 8;
	public static final int CARD_TYPE_SAME_COLOR_FLUSH_VALUE = 9;
	public static final int CARD_TYPE_BIG_COMMON_BOMB_VALUE = 10;
	public static final int CARD_TYPE_KING_OF_BOMB_VALUE = 11;
	
	private int  value;
	private String  desc;
	
	private CardTypeEnum(int nValueParam, String strDescParam){
		this.value = nValueParam;
		this.desc = strDescParam;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getDesc(){
		return this.desc;
	}
	
	public static int getMinCardType(){
		return CARD_TYPE_ONE_PIECE_VALUE;
	}
	
	public static int getMaxCardType(){
		return CARD_TYPE_KING_OF_BOMB_VALUE;
	}
	
	public static boolean isStatisfyBomb(int nPorkerNum){
		if(nPorkerNum < 4){
			return false;
		}
		
		return true;
	}
}
