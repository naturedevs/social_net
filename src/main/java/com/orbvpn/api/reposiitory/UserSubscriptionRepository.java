package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import com.orbvpn.api.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Integer> {

    long deleteByUser(User user);

    long deleteByUserId(int id);

    UserSubscription findFirstByUserOrderByCreatedAtDesc(User user);

    @Query("select count(sub.id) from UserSubscription sub where sub.createdAt > :createdAt")
    int countTotalSubscriptionCount(LocalDateTime createdAt);

    @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt")
    BigDecimal getTotalSubscriptionPrice(LocalDateTime createdAt);

    // Resellers queries

    @Query("select count(sub.id) from UserSubscription sub where sub.expiresAt > current_date and sub.user.reseller = :reseller")
    BigDecimal countResellerActiveSubscriptions(Reseller reseller);

    @Query("select count(sub.id) from UserSubscription sub where sub.expiresAt > current_date and sub.user.reseller.level <> 'OWNER'")
    BigDecimal countAllResellersActiveSubscriptions();

    @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.user.reseller = :reseller")
    BigDecimal getResellerTotalSale(Reseller reseller, LocalDateTime createdAt);

    @Query("select sum(sub.price) from UserSubscription sub where sub.createdAt > :createdAt and sub.user.reseller.level <> 'OWNER'")
    BigDecimal getAllResellerTotalSale(LocalDateTime createdAt);

    @Query("select sub.user.profile from UserSubscription sub where sub.expiresAt >= :startTime and sub.expiresAt <= :endTime")
    List<UserProfile> getUsersExpireBetween(LocalDateTime startTime, LocalDateTime endTime);
}
