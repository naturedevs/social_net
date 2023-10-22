package com.orbvpn.api.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationSetup {
    @Value("${application.default-timezone}")
    private String defaultTimeZone;

    @PostConstruct
    public void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
        log.info("Default time zone is set to " + TimeZone.getDefault().getDisplayName());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        log.info("App startup.");
    }

}
