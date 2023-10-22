package com.orbvpn.api.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;

    private static final String DISCLAIMER = "Disclaimer: This message is intended only for the individual(s) named and may contain confidential information. If you have received this e-mail in error you should not read, print, copy or forward it. Please notify the sender about the error immediately by e-mail and delete this message from your system.";

    public void sendMail(String fromEmail, String toEmail, String subject, String body, String... attachedFile ) {
        try {
            log.debug("Sending Email to {}...", toEmail);

            body = "<img class=\"logo\" src=\"cid:logo\"><br><br>" + body;
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.addInline("logo", new ClassPathResource("/image/logo.svg"));
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            if(attachedFile.length > 0) {
                String[] path = attachedFile[0].split("_");
                messageHelper.addAttachment(path[path.length - 1], getAttachedFile(attachedFile[0]));
            }

            mailSender.send(message);
            log.debug("Email sent successfully to {}", toEmail);
        } catch (MessagingException ex) {
            log.error("Failed to send email to {}", toEmail, ex);
        }
    }

    public ByteArrayResource getAttachedFile(String filename) {
        try {
            Path path = Paths.get(filename);
            byte[] content = Files.readAllBytes(path);
            return new ByteArrayResource(content);
        } catch (Exception e) {
            log.error("Error while getting the file.", e);
            return null;
        }
    }
}
