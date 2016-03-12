package com.lbwan.game.redisTest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbwan.game.service.BaseService;

/**
 * 登录 Service 
 * 
 * @author chennq
 *
 * 2013-7-30
 */
@Service
public class LoginService extends BaseService{
    
    private static final String PREFIX_ANONYMOUS_PLAYER_NICKNAME = "游客";

    private static final String KEY_ANONYMOUS_PLAYER = "anonymous_player:id";
    
    @Autowired
    private LoginDao loginDao;
    
    
    public long getPlayerId(String username){
        User user = loginDao.getUser(username);
        if(user != null){
            return user.getPlayerId();
        }
        return 0;
    }
    
    /**
     * 查找玩家信息，如果不存在，则创建一个游客信息
     * @param username
     * @return
     */
    public void getOrCreatePlayer(String username,int platForm){
        User user = loginDao.getUser(username);
        if(user == null){
            user = new User();
            user.setUsername(username);
            user.setState(UserStateEnum.ENABLE);
            loginDao.saveUser(user);
        }
        
//        Player player = null;
//        if(user.getPlayerId() > 0){
//            player = playerService.getPlayer(user.getPlayerId());
//        }else{
//            String nickName = PREFIX_ANONYMOUS_PLAYER_NICKNAME + loginDao.createId(KEY_ANONYMOUS_PLAYER);
//            player = playerService.createPlayer(username, nickName,platForm);
//        }
//        return player;
    }

    public LoginDao getLoginDao() {
        return loginDao;
    }

    public void setLoginDao(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    
}
