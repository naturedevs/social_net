package com.orbvpn.api.config.logging;

import ch.qos.logback.core.rolling.RollingFileAppender;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Atefeh Zareh
 */
public abstract class InternalRollingFileAppender extends RollingFileAppender {
    // private static ResourceBundle bundle =
    // ResourceBundle.getBundle("config.ApplicationProperties");
    private final String appVersion;
    private final String appBuildDate;

    public InternalRollingFileAppender() {
        appBuildDate = ""; // bundle.getString("timestamp");
        appVersion = ""; // bundle.getString("version");
    }

    public abstract String getAppName();

    @Override
    public void openFile(String fileName) throws IOException {
        super.openFile(fileName);
        File activeFile = new File(getFile());
        if (activeFile.exists() && activeFile.isFile() && activeFile.length() == 0) {
            StringBuilder stringBuilder = new StringBuilder("-------------------------------------\n");
            stringBuilder.append(getAppName() + " version: " + appVersion +
                    "\n" + "Build date: " + appBuildDate + "\n");
            stringBuilder.append("-------------------------------------\n");
            FileUtils.writeStringToFile(activeFile, stringBuilder.toString(), StandardCharsets.UTF_8);
        }
    }
}
