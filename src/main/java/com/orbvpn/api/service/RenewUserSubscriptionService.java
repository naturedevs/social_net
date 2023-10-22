package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.exception.InsufficientFundsException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.UserSubscriptionViewMapper;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.service.payment.PaymentService;
import com.orbvpn.api.service.reseller.ResellerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RenewUserSubscriptionService {

    private final UserSubscriptionViewMapper subscriptionViewMapper;
    private final ResellerUserService resellerUserService;
    private final UserSubscriptionService subscriptionService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final RadiusService radiusService;
    private final ResellerRepository resellerRepository;

    public void renewSubscriptions() {

        List<Payment> paymentsToRenew = paymentService.findAllSubscriptionPaymentsToRenew();

        for (Payment payment : paymentsToRenew) {
            renewPayment(payment);
        }
    }

    public Payment renewPayment(Payment payment) {

        try {
            return paymentService.renewPayment(payment);
        } catch (Exception ex) {
            log.error("Couldn't renew user subscription. {}", ex.getMessage());
            return null;
        }
    }

    public UserSubscriptionView renewWithDayCount(User user, Integer days) {

        UserSubscription subscription = subscriptionService.getCurrentSubscription(user);

        //decrease reseller's credit
        Reseller reseller = user.getReseller();
        BigDecimal credit = reseller.getCredit();
        BigDecimal price = calculatePrice(reseller, subscription.getGroup(), days);
        if (credit.compareTo(price) < 0) {
            throw new InsufficientFundsException();
        }
        reseller.setCredit(credit.subtract(price));
        resellerRepository.save(reseller);

        // Reset subscription duration and Price
        subscription.extendDuration(days);
        var subscriptionPrice = subscription.getPrice();
        if (subscriptionPrice == null) subscriptionPrice = BigDecimal.ZERO;
        subscription.setPrice(subscriptionPrice.add(price));

        subscriptionService.saveUserSubscription(subscription);

        // Reset Radcheck expiration Date
        radiusService.updateUserExpirationRadCheck(subscription);

        // Reset Payment Price and ExpiresAt
        Payment payment = subscription.getPayment();
        if (payment != null) {
            payment.setPrice(payment.getPrice().add(price));
            payment.setExpiresAt(payment.getExpiresAt().plusDays(days));
        } else {
            payment = Payment.builder()
                    .user(user)
                    .status(PaymentStatus.PENDING)
                    .gateway(GatewayName.RESELLER_CREDIT)
                    .category(PaymentCategory.GROUP)
                    .moreLoginCount(subscription.getGroup().getMultiLoginCount())
                    .price(price)
                    .groupId(subscription.getGroup().getId())
                    .renew(true)
                    .build();
        }
        paymentRepository.save(payment);

        log.info("The subscription period of User {} has increased by {} days.", user.getId(), days);
        return subscriptionViewMapper.toView(subscription);
    }

    public BigDecimal calculatePrice(Reseller reseller, Group group, Integer days) {
        ResellerLevel level = reseller.getLevel();
        if (level.getName() == ResellerLevelName.OWNER) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = group.getPrice();
        BigDecimal discount = price.multiply(level.getDiscountPercent()).divide(new BigDecimal(100));
        BigDecimal dayPercent = BigDecimal.valueOf(days).divide(BigDecimal.valueOf(group.getDuration()));

        return price.subtract(discount).multiply(dayPercent);
    }

    public UserSubscriptionView resetUserSubscription(User user) {

        log.info("User {}'s subscription will reset with the current group.", user.getId());

        Group currentGroup = subscriptionService.getCurrentSubscription(user).getGroup();

        return resetUserSubscription(user, currentGroup.getId());
    }

    public UserSubscriptionView resetUserSubscription(User user, int groupId) {

        log.info("User {}'s subscription will reset with group id {}", user.getId(), groupId);

        UserSubscription subscription = subscriptionService.getCurrentSubscription(user);
        Payment currentPayment = subscription.getPayment();
        if (currentPayment == null) {
            throw new NotFoundException("Subscription of this user does not have a Payment!");
        }

        currentPayment.setGroupId(groupId);
        subscription.setPayment(currentPayment);

        renewPayment(subscription.getPayment());
        UserSubscription newSubscription = subscriptionService.getCurrentSubscription(user);
        radiusService.updateUserExpirationRadCheck(newSubscription);

        return subscriptionViewMapper.toView(newSubscription);
    }

    public UserSubscriptionView resellerRenewUserSubscription(User user) {

        log.info("Reseller will renew User {}'s subscription with the current group.", user.getId());

        Group currentGroup = subscriptionService.getCurrentSubscription(user).getGroup();

        resellerUserService.createResellerUserSubscription(user, currentGroup);

        UserSubscription newSubscription = subscriptionService.getCurrentSubscription(user);
        radiusService.updateUserExpirationRadCheck(newSubscription);

        return subscriptionViewMapper.toView(newSubscription);
    }

    public UserSubscriptionView resellerRenewUserSubscription(User user, Group group) {

        log.info("Reseller will renew User {}'s subscription with group id {}", user.getId(), group.getId());

        resellerUserService.createResellerUserSubscription(user, group);

        UserSubscription newSubscription = subscriptionService.getCurrentSubscription(user);
        radiusService.updateUserExpirationRadCheck(newSubscription);

        return subscriptionViewMapper.toView(newSubscription);
    }
}
