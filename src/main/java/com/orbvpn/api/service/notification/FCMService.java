package com.orbvpn.api.service.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.orbvpn.api.domain.dto.FcmNotificationDto;
import com.orbvpn.api.domain.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private static final String TOKEN_EMPTY = "Can not send push notification to null or empty token!";
    private static final String ERROR_MESSAGE = "Could not send notification to {%s}. Error : %s";

    private final FirebaseMessaging firebaseMessaging;

    public FcmNotificationDto sendNotification(NotificationDto notificationDto, String token) {

        if (token == null || token.length() == 0) {
            log.error(TOKEN_EMPTY);
            return new FcmNotificationDto(TOKEN_EMPTY);
        }

        if (notificationDto.getData() == null) {
            notificationDto.setData(Collections.emptyMap());
        }

        Notification notification = Notification
                .builder()
                .setTitle(notificationDto.getSubject())
                .setBody(notificationDto.getContent())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(notificationDto.getData())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            return new FcmNotificationDto(String.format(ERROR_MESSAGE, token, e.getMessagingErrorCode()));
        } catch (Exception e) {
            return new FcmNotificationDto(String.format(ERROR_MESSAGE, token, e.getMessage()));
        }
        return new FcmNotificationDto();
    }

    public void sendLogoutNotification(String fcmToken) {

        if (fcmToken == null || fcmToken.length() == 0) {
            log.error("Can not send push notification to null or empty token!");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("action", "exit");
        data.put("title", "Logout");
        data.put("body", "Your device has been logged out.");

        NotificationDto notificationDto = NotificationDto.builder()
                .subject("Logout")
                .content("Your device has been logged out.")
                .data(data)
                .build();

        sendNotification(notificationDto, fcmToken);
    }

    public FcmNotificationDto sendBulkNotification(NotificationDto notificationDto, List<String> tokens) {

        int notificationCount = 0;
        for (String token : tokens)
            if (sendNotification(notificationDto, token).getStatus())
                notificationCount += 1;

        return new FcmNotificationDto(notificationCount);
    }
}
