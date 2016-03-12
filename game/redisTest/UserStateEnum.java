package com.lbwan.game.redisTest;

import com.lbwan.game.core.redis.SerializableEnum;

/**
 * 用户状态
 * 
 * @author chennq
 *
 * 2013-7-30
 */
public enum UserStateEnum implements SerializableEnum {
    
    /**
     * 启用，正常
     */
    ENABLE(1),
    
    /**
     * 禁用
     */
    DISABLE(2);
    
    private int value;
    
    private UserStateEnum(int value){
        this.value = value;
    }
    
    @Override
    public int getValue(){
        return this.value;
    }
}
