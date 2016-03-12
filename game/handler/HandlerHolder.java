package com.lbwan.game.handler;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.lbwan.game.modules.handler.GameHttpHandler;
import com.lbwan.game.modules.handler.GameRoomHandler;
import com.lbwan.game.modules.handler.HostingPorkerHandler;
import com.lbwan.game.modules.handler.ProcessTributeHandler;
import com.lbwan.game.modules.handler.WhiipedEggHandler;
import com.lbwan.game.modules.handler.TalkHandler;

import com.lbwan.game.proto.CmdProtocol.CmdType;
import com.lbwan.game.proto.GameRoomProtocol.CmdHttpType;
import com.lbwan.game.proto.GameRoomProtocol.CmdRoomType;
import com.lbwan.game.spring.SpringUtils;


/**
 * The Class HandlerHolder.
 * @author hli
 */
@Component
public class HandlerHolder {

    /** The handler map. */
    private static final HashMap<Integer, Handler> handlerMap = new HashMap<>();

    /** The http handler map. */
    private static final HashMap<Integer, HttpHandler> httpHandlerMap = new HashMap<>();
    
    /**
     * Inits the handler map. 
     */
    private synchronized static void initHandlerMap() {
        if (false == handlerMap.isEmpty()) {
            return;
        }

        handlerMap.put(CmdRoomType.CMD_CREATE_GAME_VALUE, (GameRoomHandler) SpringUtils.getBeanByName("gameRoomHandler"));
        handlerMap.put(CmdRoomType.CMD_JOIN_GAME_VALUE, (GameRoomHandler) SpringUtils.getBeanByName("gameRoomHandler"));
        handlerMap.put(CmdRoomType.CMD_READY_GAME_VALUE, (GameRoomHandler) SpringUtils.getBeanByName("gameRoomHandler"));
        handlerMap.put(CmdRoomType.CMD_EXIT_GAME_VALUE, (GameRoomHandler) SpringUtils.getBeanByName("gameRoomHandler"));
        
        handlerMap.put(CmdType.CMD_SUMBIT_SOME_PORKER_VALUE, (WhiipedEggHandler) SpringUtils.getBeanByName("whiipedEggHandler"));
        handlerMap.put(CmdType.CMD_SUMBIT_NULL_PORKER_VALUE, (WhiipedEggHandler) SpringUtils.getBeanByName("whiipedEggHandler"));
        handlerMap.put(CmdType.CMD_REQUEST_GAME_INIT_INFO_VALUE, (WhiipedEggHandler) SpringUtils.getBeanByName("whiipedEggHandler"));
        
        handlerMap.put(CmdType.CMD_PAY_TRIBUTE_REQUEST_VALUE, (ProcessTributeHandler) SpringUtils.getBeanByName("processTributeHandler"));
        handlerMap.put(CmdType.CMD_BACK_TRIBUTE_REQUEST_VALUE, (ProcessTributeHandler) SpringUtils.getBeanByName("processTributeHandler"));
        
        handlerMap.put(CmdType.CMD_NEED_HOSTING_PORKER_VALUE, (HostingPorkerHandler) SpringUtils.getBeanByName("hostingPorkerHandler"));
        handlerMap.put(CmdType.CMD_CANCEL_HOSTING_PORKER_VALUE, (HostingPorkerHandler) SpringUtils.getBeanByName("hostingPorkerHandler"));
        
        handlerMap.put(CmdType.CMD_GAME_SEND_CHAT_VALUE, (TalkHandler) SpringUtils.getBeanByName("talkHandler"));
    }

    /**
     * Gets the handler.
     *
     * @param key the key
     * @return the handler
     */
    public Handler getHandler(Integer key) {
        if (handlerMap.isEmpty()) {
            initHandlerMap();
        }
        return handlerMap.get(key);
    }
    
    private synchronized static void initHttpHandlerMap() {
        if (false == httpHandlerMap.isEmpty()) {
            return;
        }
       
        httpHandlerMap.put(CmdHttpType.CMD_HTTP_CHECK_LAST_GAME_VALUE,
                (GameHttpHandler) SpringUtils.getBeanByName("gameHttpHandler"));

        httpHandlerMap.put(CmdHttpType.CMD_HTTP_GET_ROOM_COUNT_VALUE,
                (GameHttpHandler) SpringUtils.getBeanByName("gameHttpHandler"));

        httpHandlerMap.put(CmdHttpType.CMD_HTTP_GET_PLAYER_COUNT_VALUE,
                (GameHttpHandler) SpringUtils.getBeanByName("gameHttpHandler"));
    }
    
    
    public HttpHandler getHttpHandler(Integer key) {
        if (httpHandlerMap.isEmpty()) {
            initHttpHandlerMap();
        }
        return httpHandlerMap.get(key);
    }
}

