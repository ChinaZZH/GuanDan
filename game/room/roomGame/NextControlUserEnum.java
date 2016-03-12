package com.lbwan.game.room.roomGame;

public enum NextControlUserEnum {
	NextControlEnum_Fail(0, "异常"),
	NextControlEnum_AnyPorker(1, "任意手牌"),
	NextControlEnum_BiggerPorker(2, "比他大的牌"),
	;
	
	public static final int FAIL          = 0;
	public static final int ANY_PORKER    = 1;
	public static final int BIGGER_PORKER = 2;
	
	private final int value;
    private final String desc;
    
    private NextControlUserEnum(int code, String desc) {
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


