package com.lbwan.game.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The Class PropertiesUtils.
 * @author hli
 */

public class GDPropertiesUtils {
    /** The Constant ZK_SERVER. */
	public static final String  PAY_TRIBUTE_TIMER = "paytribute.seconds";
	
	public static final String  BACK_TRIBUTE_TIMER = "backtribute.seconds";
	
	public static final String  GAME_START_TIMER = "gamestart.timer";
	
	public static final String CLIENT_OPERATION_TIMER = "clientoperation.timer";
	
    public static final String USEROPERATION_TIMER = "useroperation.timer";
    
    public static final String OPERATION_TIMEOUT_TIMES = "operationtimeout.times";
    
    public static final String LESS_USER_OPERATION_SEC = "lessuser.sec";
    
    public static final String SERVER_CLIENTSECS = "server.clientsecs";
    
    public static final String DEFAULT_MAJOR_CARD = "major.card";
    
    public static final String BACK_TRIBUTE_MAX_VALUE = "backtribute.maxvalue";
    
    public static final String PAY_TRIBUTE_SECONDS = "paytribute.seconds";
    
    public static final String BACK_TRIBUTE_SECONDS = "backtribute.seconds";
     
    private static Logger logger = Logger.getLogger(GDPropertiesUtils.class);

    private static Properties props = new Properties();

    static {
        try {
            InputStream in = GDPropertiesUtils.class.getResourceAsStream("/config-gd.xml");
            props.loadFromXML(in);
        } catch (IOException e) {
            logger.error("when parse config.xml", e);
        }
    }

    /**
     * Gets the property as string.
     *
     * @param key the key
     * @return the property as string,if key not exist return null.
     */
    public static String getPropertyAsString(String key) {
        return props.getProperty(key);
    }

    /**
     * Gets the property as string.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property as string, if key not exist return defaultValue.
     */
    public static String getPropertyAsString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * Gets the property as integer.
     *
     * @param key the key
     * @return the property as integer
     */
    public static Integer getPropertyAsInteger(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    /**
     * Gets the property as integer.
     *
     * @param key the key
     * @param defaultValue the default value
     * @return the property as integer
     */
    public static Integer getPropertyAsInteger(String key, Integer defaultValue) {
        return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
    }
}
