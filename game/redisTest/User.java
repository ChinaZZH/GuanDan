package com.lbwan.game.redisTest;

/**
 * 
 * 用户账号信息
 * 
 * @author chennq
 *
 * 2013-7-30
 */
public class User {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 状态
     */
    private UserStateEnum state;
    
    /**
     * 对应的玩家ID
     */
    private long playerId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserStateEnum getState() {
        return state;
    }

    public void setState(UserStateEnum state) {
        this.state = state;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
    
    
    
}
