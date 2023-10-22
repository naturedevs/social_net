package com.orbvpn.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/.well-known/acme-challenge")
@Slf4j
public class AcmeChallengeController {

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/{fileName:.+}")
    public void downloadAcmeChallenge(HttpServletResponse response,
                                      @PathVariable("fileName") String fileName) throws IOException {
        //HttpServletRequest request,
        Resource resource = resourceLoader
                .getResource("classpath:deployment/" + fileName);
        if (resource.exists()) {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=" +
                            resource.getFilename()));
            response.setContentLength((int) resource.contentLength());
            InputStream inputStream = resource.getInputStream();
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } else {
            throw new RuntimeException("SSL id resource doesn't exist.");
        }
        log.info("ssl id is sent");
    }
}
