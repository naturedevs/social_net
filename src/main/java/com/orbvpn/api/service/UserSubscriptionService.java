package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.BulkSubscription;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final RadiusService radiusService;
    private final GroupService groupService;

    public UserSubscription createUserSubscription(Payment payment) {

        User user = payment.getUser();
        Group group = groupService.getById(payment.getGroupId());

        log.info("Creating subscription for user with id {} for group {}", user.getId(), group.getId());
        UserSubscription userSubscription = new UserSubscription();
        int duration = group.getDuration();

        userSubscription.setUser(user);
        userSubscription.setGroup(group);
        userSubscription.setPayment(payment);
        userSubscription.setDuration(duration);
        userSubscription.setDailyBandwidth(group.getDailyBandwidth());
        userSubscription.setDownloadUpload(group.getDownloadUpload());
        userSubscription.setMultiLoginCount(group.getMultiLoginCount());
        userSubscription.setExpiresAt(LocalDateTime.now().plusDays(duration));

        userSubscriptionRepository.deleteByUserId(user.getId());
        userSubscriptionRepository.flush();
        userSubscriptionRepository.save(userSubscription);
        radiusService.deleteUserRadChecks(user);
        radiusService.createUserRadChecks(userSubscription);
        return userSubscription;
    }

    public UserSubscription createSubscriptionByAdmin(User user, Group group) {

        log.info("Creating subscription for user with id {} for group {}", user.getId(), group.getId());
        UserSubscription userSubscription = new UserSubscription();
        int duration = group.getDuration();

        userSubscription.setUser(user);
        userSubscription.setGroup(group);
        userSubscription.setDuration(duration);
        userSubscription.setDailyBandwidth(group.getDailyBandwidth());
        userSubscription.setDownloadUpload(group.getDownloadUpload());
        userSubscription.setMultiLoginCount(group.getMultiLoginCount());
        userSubscription.setExpiresAt(LocalDateTime.now().plusDays(duration));

        userSubscriptionRepository.deleteByUserId(user.getId());
        userSubscriptionRepository.flush();
        userSubscriptionRepository.save(userSubscription);
        radiusService.deleteUserRadChecks(user);
        radiusService.createUserRadChecks(userSubscription);
        return userSubscription;
    }

    public void createBulkSubscription(User user, BulkSubscription subscription) {

        log.info("Creating subscription for user with id {} for group {}", user.getId(), subscription.getGroupId());
        UserSubscription userSubscription = new UserSubscription();
        Group group = groupService.getById(subscription.getGroupId());
        int duration = subscription.getDuration() == null ? group.getDuration() : subscription.getDuration();
        int multiLoginCount = subscription.getMultiLoginCount() == null ? group.getMultiLoginCount() : subscription.getMultiLoginCount();

        userSubscription.setUser(user);
        userSubscription.setGroup(group);
        userSubscription.setDuration(duration);
        userSubscription.setDailyBandwidth(group.getDailyBandwidth());
        userSubscription.setDownloadUpload(group.getDownloadUpload());
        userSubscription.setMultiLoginCount(multiLoginCount);
        userSubscription.setExpiresAt(LocalDateTime.now().plusDays(duration));

        userSubscriptionRepository.deleteByUserId(user.getId());
        userSubscriptionRepository.flush();
        userSubscriptionRepository.save(userSubscription);
        radiusService.deleteUserRadChecks(user);
        radiusService.createUserRadChecks(userSubscription);
    }

    public void deleteUserSubscriptions(User user) {
        userSubscriptionRepository.deleteByUser(user);
    }

    public void saveUserSubscription(UserSubscription subscription) {
        userSubscriptionRepository.save(subscription);
    }

    public void updateSubscriptionMultiLoginCount(User user, int multiLoginCount) {
        UserSubscription subscription = getCurrentSubscription(user);
        subscription.setMultiLoginCount(multiLoginCount);
        userSubscriptionRepository.save(subscription);
        radiusService.editUserMoreLoginCount(user, multiLoginCount);
    }

    public UserSubscription getCurrentSubscription(User user) {
        return userSubscriptionRepository.findFirstByUserOrderByCreatedAtDesc(user);
    }

    public List<UserProfile> getUsersExpireBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return userSubscriptionRepository.getUsersExpireBetween(startTime, endTime);
    }

    public List<UserProfile> getUsersExpireAt(LocalDate localDate) {
        LocalDateTime startTime = localDate.atStartOfDay();
        LocalDateTime endTime = localDate.plusDays(1).atStartOfDay();
        return getUsersExpireBetween(startTime, endTime);
    }

    public List<UserProfile> getUsersExpireInNextDays(Integer dayCount) {
        LocalDate localDate = LocalDate.now().plusDays(dayCount);
        return getUsersExpireAt(localDate);
    }

    public List<UserProfile> getUsersExpireInPreviousDays(Integer dayCount) {
        LocalDate localDate = LocalDate.now().minusDays(dayCount);
        return getUsersExpireAt(localDate);
    }

    public void save(UserSubscription userSubscription) {
        userSubscriptionRepository.deleteByUserId(userSubscription.getUser().getId());
        userSubscriptionRepository.flush();
        userSubscriptionRepository.save(userSubscription);
    }

}

