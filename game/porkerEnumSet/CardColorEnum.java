package com.lbwan.game.porkerEnumSet;

public enum CardColorEnum {
	CardColorEnum_Error(-1, "错误/异常"),
	CardColorEnum_Null(0, "空颜色/大小王"),
	CardColorEnum_Heart(1, "红桃"),
	CardColorEnum_Diamond(2, "方块"),
	CardColorEnum_Spade(3, "黑桃"),
	CardColorEnum_Club(4, "草花"),
	;
	
	public static final int CARD_COLOR_ERROR    = -1;
	public static final int CARD_COLOR_NULL     = 0;
	public static final int CARD_COLOR_HEART    = 1;
	public static final int CARD_COLOR_DIAMOND  = 2;
	public static final int CARD_COLOR_SPADE    = 3;
	public static final int CARD_COLOR_CLUB     = 4;
	
	private final int value;
    private final String desc;
    
    private CardColorEnum(int code, String desc) {
        this.value = code;
        this.desc = desc;
    }
    
    public int getValue(){
		return this.value;
	}
	
	public String getDesc(){
		return this.desc;
	}
}
