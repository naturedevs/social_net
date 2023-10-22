package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.domain.dto.BuyMoreLoginsView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.time.temporal.ChronoUnit.DAYS;

@RequiredArgsConstructor
@Service
public class AccountingService {
  private final UserRepository userRepository;
  private final UserSubscriptionRepository userSubscriptionRepository;
  private final PaymentRepository paymentRepository;

  private final UserService userService;
  private final UserSubscriptionService userSubscriptionService;
  private final ServiceGroupService serviceGroupService;

  public AccountingView getAccounting() {
    AccountingView accountingView = new AccountingView();

    LocalDateTime dateTime = LocalDateTime.now();
    LocalDateTime currentDay = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 0, 0);
    LocalDateTime currentMonth = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), 1, 0 ,0 );
    LocalDateTime currentYear = LocalDateTime.of(dateTime.getYear(), 1, 1, 0, 0);

    int totalUsers = (int) userRepository.count();
    int joinedByDay = (int) userRepository.countByCreatedAtAfter(currentDay);
    int joinedByMonth = (int) userRepository.countByCreatedAtAfter(currentMonth);
    int joinedByYear = (int) userRepository.countByCreatedAtAfter(currentYear);
    int monthPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentMonth);
    BigDecimal monthPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentMonth);
    int dayPurchaseCount = userSubscriptionRepository.countTotalSubscriptionCount(currentDay);
    BigDecimal dayPurchase = userSubscriptionRepository.getTotalSubscriptionPrice(currentDay);

    int monthRenewPurchaseCount = paymentRepository.getTotalRenewSubscriptionCount(currentMonth);
    BigDecimal monthRenewPurchase = paymentRepository.getTotalRenewSubscriptionPrice(currentMonth);
    int dayRenewPurchaseCount = paymentRepository.getTotalRenewSubscriptionCount(currentDay);
    BigDecimal dayRenewPurchase = paymentRepository.getTotalRenewSubscriptionPrice(currentDay);


    accountingView.setTotalUsers(totalUsers);
    accountingView.setJoinedByDay(joinedByDay);
    accountingView.setJoinedByMonth(joinedByMonth);
    accountingView.setJoinedByYear(joinedByYear);
    accountingView.setMonthPurchaseCount(monthPurchaseCount);
    accountingView.setMonthPurchase(monthPurchase);
    accountingView.setDayPurchaseCount(dayPurchaseCount);
    accountingView.setDayPurchase(dayPurchase);
    accountingView.setMonthRenewPurchaseCount(monthRenewPurchaseCount);
    accountingView.setMonthRenewPurchase(monthRenewPurchase);
    accountingView.setDayRenewPurchaseCount(dayRenewPurchaseCount);
    accountingView.setDayRenewPurchase(dayRenewPurchase);

    return accountingView;
  }

  public BuyMoreLoginsView getBuyMoreLogins(){
    User user = userService.getUser();

    UserSubscription userSubscription = userSubscriptionService.getCurrentSubscription(user);
    LocalDateTime expiresAt = userSubscription.getExpiresAt();

    LocalDateTime now = LocalDateTime.now();
    long daysUntilExpiration = DAYS.between(now, expiresAt);
    BuyMoreLoginsView buyMoreLoginsView = new BuyMoreLoginsView();
    double priceForMoreLogins = 0;
    try{
      if (daysUntilExpiration > 0)
      {
        Group userGroup = userSubscription.getGroup();
        double discountRateForServiceGroup =  userGroup.getServiceGroup().getDiscount().doubleValue() / 100;
        int durationForAccount = userSubscription.getDuration();
        BigDecimal groupPrice = userGroup.getPrice();
        if (durationForAccount > 0) {
          double groupPricePerDay = groupPrice.doubleValue() * (1 - discountRateForServiceGroup) / durationForAccount;
          priceForMoreLogins = groupPricePerDay * daysUntilExpiration * (1 - discountRateForServiceGroup);
        } else {
          buyMoreLoginsView.setMessage("Your account has expired.");
        }
      } else {
        buyMoreLoginsView.setMessage("Your account has expired.");
      }
    } catch (Exception e) {

    }
    buyMoreLoginsView.setPriceForMoreLogins(priceForMoreLogins);
    return buyMoreLoginsView;
  }
}
