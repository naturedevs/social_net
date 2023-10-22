package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserDevice;
import com.orbvpn.api.exception.AccessDeniedException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.exception.UnauthenticatedAccessException;
import com.orbvpn.api.mapper.UserDeviceMapper;
import com.orbvpn.api.reposiitory.UserDeviceRepository;
import com.orbvpn.api.service.notification.FCMService;
import com.orbvpn.api.service.reseller.ResellerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final ResellerUserService resellerUserService;
    private final UserService userService;
    private final FCMService fcmService;
    private final UserDeviceMapper userDeviceMapper;
    private final Lock lock = new ReentrantLock(true);

    public UserDeviceView loginDevice(UserDeviceDto userDeviceDto) {

        User user = userService.getUser();
        UserSubscriptionView subscription = userService.getUserSubscription(user);
        lock.lock();
        try {
            List<UserDevice> userDevices = userDeviceRepository.getUserDeviceByUser(user);
            List<UserDevice> activeDevices = userDevices.stream()
                    .filter(UserDevice::getIsActive)
                    .collect(Collectors.toList());

            UserDevice currentDevice = userDevices.stream()
                    .filter(userDevice -> userDevice.getDeviceId().equals(userDeviceDto.getDeviceId()))
                    .findAny()
                    .orElse(null);

            if (currentDevice == null) {
                if (subscription.getMultiLoginCount() <= activeDevices.size()) {
                    throw new UnauthenticatedAccessException("You've reached the number of devices you can login to!");
                } else {
                    currentDevice = userDeviceMapper.toUserDevice(userDeviceDto);
                    currentDevice.setUser(user);
                }
            } else {
                if (Boolean.TRUE.equals(currentDevice.getIsBlocked()))
                    throw new AccessDeniedException("This device is blocked!");

                int activeDeviceCount = currentDevice.getIsActive() ? activeDevices.size() - 1 : activeDevices.size();
                if (subscription.getMultiLoginCount() <= activeDeviceCount)
                    throw new UnauthenticatedAccessException("You've reached the number of devices you can login to!");

                currentDevice.setAppVersion(userDeviceDto.getAppVersion());
                currentDevice.setDeviceName(userDeviceDto.getDeviceName());
                currentDevice.setDeviceModel(userDeviceDto.getDeviceModel());
                currentDevice.setOs(currentDevice.getOs());
            }
            currentDevice.setFcmToken(userDeviceDto.getFcmToken());
            currentDevice.setLoginDate(LocalDateTime.now());
            currentDevice.setLogoutDate(null);
            currentDevice.setIsActive(true);
            currentDevice = userDeviceRepository.save(currentDevice);
            return userDeviceMapper.toUserDeviceView(currentDevice);
        } finally {
            lock.unlock();
        }
    }

    public UserDeviceView logoutDevice(Long userDeviceId) {
        UserDevice userDevice = userDeviceRepository.getUserDeviceById(userDeviceId)
                .orElseThrow(() -> new NotFoundException(UserDevice.class, userDeviceId));
        return logoutDevice(userDevice);
    }

    public UserDeviceView logoutDevice(String deviceId) {
        UserDevice userDevice = userDeviceRepository.findFirstByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(UserDevice.class, deviceId));
        return logoutDevice(userDevice);
    }

    public UserDeviceView logoutDevice(UserDevice userDevice) {
        userDevice.setLogoutDate(LocalDateTime.now());
        userDevice.setIsActive(false);
        userDevice = userDeviceRepository.save(userDevice);

        fcmService.sendLogoutNotification(userDevice.getFcmToken());
        return userDeviceMapper.toUserDeviceView(userDevice);
    }

    public UserDeviceView resellerLogoutDevice(int userId, String deviceId) {
        User user = userService.getUserById(userId);
        resellerUserService.checkResellerUserAccess(user);
        return logoutDevice(deviceId);
    }

    public List<UserDeviceView> getActiveDevices() {
        User user = userService.getUser();
        return userDeviceRepository.getUserDeviceByUser(user).stream()
                .filter(UserDevice::getIsActive)
                .map(userDeviceMapper::toUserDeviceView)
                .collect(Collectors.toList());
    }

    public List<UserDeviceView> resellerGetActiveDevices(int userId) {
        User user = userService.getUserById(userId);
        resellerUserService.checkResellerUserAccess(user);
        return userDeviceRepository.getUserDeviceByUser(user).stream()
                .filter(UserDevice::getIsActive)
                .map(userDeviceMapper::toUserDeviceView)
                .collect(Collectors.toList());
    }

    public UserDeviceView blockDevice(String deviceId) {
        UserDevice userDevice = userDeviceRepository.findFirstByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(UserDevice.class, deviceId));

        resellerUserService.checkResellerUserAccess(userDevice.getUser());
        userDevice.setIsBlocked(true);
        return logoutDevice(userDevice);
    }

    public UserDeviceView unblockDevice(String deviceId) {
        UserDevice userDevice = userDeviceRepository.findFirstByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(UserDevice.class, deviceId));

        resellerUserService.checkResellerUserAccess(userDevice.getUser());
        userDevice.setIsBlocked(false);
        userDevice = userDeviceRepository.save(userDevice);
        return userDeviceMapper.toUserDeviceView(userDevice);
    }

    public FcmNotificationDto sendNotificationByDeviceId(String deviceId, NotificationDto notificationDto) {
        UserDevice userDevice = userDeviceRepository.findFirstByDeviceId(deviceId)
                .orElseThrow(() -> new NotFoundException(UserDevice.class, deviceId));
        return fcmService.sendNotification(notificationDto, userDevice.getFcmToken());
    }

    public FcmNotificationDto adminSendNotificationByToken(String fcmToken, NotificationDto notificationDto) {
        return fcmService.sendNotification(notificationDto, fcmToken);
    }

    public List<UserDeviceView> getAllActiveDevices() {
        return userDeviceRepository.findAll().stream()
                .filter(userDevice -> !userDevice.getIsBlocked())
                .map(userDeviceMapper::toUserDeviceView)
                .collect(Collectors.toList());
    }

    public FcmNotificationDto adminSendNotificationToAll(NotificationDto notificationDto) {
        List<String> tokens = getAllActiveDevices().stream()
                .map(UserDeviceView::getFcmToken)
                .collect(Collectors.toList());

        return fcmService.sendBulkNotification(notificationDto, tokens);
    }
}
