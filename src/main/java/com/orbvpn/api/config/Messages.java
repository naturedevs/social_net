package com.orbvpn.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
public class Messages {

    private static final ResourceBundleMessageSource source;

    static {
        source = new ResourceBundleMessageSource();
        source.setBasename("messages/messages");
        source.setUseCodeAsDefaultMessage(true);
    }

    public static String getMessage(String code) {
        return source.getMessage(code, null, Locale.ENGLISH);
    }

}
