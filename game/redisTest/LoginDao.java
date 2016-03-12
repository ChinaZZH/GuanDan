package com.lbwan.game.redisTest;

import org.springframework.stereotype.Repository;

import com.lbwan.game.core.redis.RedisBaseDao;

/**
 * 登录 Dao
 * 
 * @author chennq
 *
 * 2013-7-30
 */
@Repository
public class LoginDao extends RedisBaseDao{
    
    private static final String KEY_USER = "user:";
    
    public User getUser(String username){
        return hgetAll(KEY_USER + username, User.class);
    }
    
    public void saveUser(User user){
        hmset(KEY_USER + user.getUsername(), user);
    }
}
