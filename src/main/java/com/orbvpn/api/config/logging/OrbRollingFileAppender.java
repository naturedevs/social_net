package com.orbvpn.api.config.logging;

/**
 * @author Atefeh Zareh
 */
public class OrbRollingFileAppender extends InternalRollingFileAppender {
    @Override
    public String getAppName() {
        return "ORB";
    }
}
