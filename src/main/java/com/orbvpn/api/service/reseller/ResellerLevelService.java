package com.orbvpn.api.service.reseller;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.entity.ResellerLevelCoefficients;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.reposiitory.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerLevelService {

  private static final BigDecimal BIG_DECIMAL_100 = BigDecimal.valueOf(100);
  private static final BigDecimal BIG_DECIMAL_HALF = new BigDecimal("0.5");

  private final UserSubscriptionRepository userSubscriptionRepository;
  private final ResellerRepository resellerRepository;
  private final ResellerLevelRepository resellerLevelRepository;
  private final ResellerAddCreditRepository resellerAddCreditRepository;
  private final ResellerLevelCoefficientsRepository resellerLevelCoefficientsRepository;


  public void updateResellersLevel() {
    LocalDateTime monthBefore = LocalDateTime.now().minusMonths(1L);
    List<Reseller> resellers = resellerRepository.findByLevelSetDateBefore(monthBefore);
    ResellerLevelCoefficients resellerLevelCoefficients = resellerLevelCoefficientsRepository.getOne(1);
    for (Reseller reseller : resellers) {
      ResellerLevel level = getResellerLevel(reseller, resellerLevelCoefficients);
      reseller.setLevel(level);
      reseller.setLevelSetDate(LocalDateTime.now());
    }
    resellerRepository.saveAll(resellers);
  }

  private ResellerLevel getResellerLevel(Reseller reseller, ResellerLevelCoefficients coefficients) {
    ResellerLevel level = reseller.getLevel();
    if (level.getName() == ResellerLevelName.OWNER) {
      return level;
    }

    BigDecimal totalScore = BigDecimal.ZERO;

    List<Reseller> resellers = resellerRepository.findAll()
      .stream()
      .filter(it -> it.getLevel().getName() != ResellerLevelName.OWNER)
      .collect(Collectors.toList());
    BigDecimal resellersCount = BigDecimal.valueOf(resellers.size() - 1);
    LocalDateTime lastSetDate = reseller.getLevelSetDate();
    LocalDateTime now = LocalDateTime.now();

    // Calculate credit month score
    BigDecimal monthCredit = resellerAddCreditRepository.getResellerCreditAfterDate(reseller, lastSetDate);
    BigDecimal monthCreditTotal = resellerAddCreditRepository.getAllResellersTotalCreditAfterDate(lastSetDate);
    BigDecimal avgCredit = monthCreditTotal.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal monthCreditPercent = coefficients.getMonthCreditPercent();
    BigDecimal monthCreditMax = coefficients.getMonthCreditMax();
    BigDecimal monthScore = calculateScore(monthCredit, avgCredit, monthCreditPercent, monthCreditMax);
    totalScore = totalScore.add(monthScore);

    // Calculate balance score
    BigDecimal balance = reseller.getCredit();
    BigDecimal totalBalance = BigDecimal.ZERO;
    for (Reseller it : resellers) {
      totalBalance = totalBalance.add(it.getCredit());
    }
    BigDecimal avgBalance = totalBalance.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal currentCreditPercent = coefficients.getCurrentCreditPercent();
    BigDecimal currentCreditMax = coefficients.getCurrentCreditMax();
    BigDecimal balanceScore = calculateScore(balance, avgBalance, currentCreditPercent, currentCreditMax);
    totalScore = totalScore.add(balanceScore);

    // Active subscription score
    BigDecimal activeSubscriptions = userSubscriptionRepository
      .countResellerActiveSubscriptions(reseller);
    BigDecimal totalActiveSubscription = userSubscriptionRepository.countAllResellersActiveSubscriptions();
    BigDecimal avgActiveSubscriptions = totalActiveSubscription.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal activeSubscriptionPercent = coefficients.getActiveSubscriptionPercent();
    BigDecimal activeSubscriptionMax = coefficients.getActiveSubscriptionMax();
    BigDecimal subscriptionScore = calculateScore(activeSubscriptions, avgActiveSubscriptions, activeSubscriptionPercent, activeSubscriptionMax);
    totalScore = totalScore.add(subscriptionScore);

    // Membership duration score
    BigDecimal membershipDuration = BigDecimal.valueOf(DAYS.between(reseller.getCreatedAt(), now));
    BigDecimal membershipTotalDuration = BigDecimal.ZERO;
    for (Reseller it : resellers) {
      long duration = DAYS.between(it.getCreatedAt(), now);
      membershipTotalDuration = membershipTotalDuration.add(BigDecimal.valueOf(duration));
    }
    BigDecimal avgDuration = membershipTotalDuration.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal membershipDurationPercent = coefficients.getMembershipDurationPercent();
    BigDecimal membershipDurationMax = coefficients.getMembershipDurationMax();
    BigDecimal membershipScore = calculateScore(membershipDuration, avgDuration, membershipDurationPercent, membershipDurationMax);
    totalScore = totalScore.add(membershipScore);

    // Deposit Intervals score
    BigDecimal deposits = resellerAddCreditRepository.countResellerDeposits(reseller);
    BigDecimal depositInterval = membershipDuration.divide(deposits, RoundingMode.HALF_UP);
    BigDecimal depositIntervalManualDays = coefficients.getDepositIntervalManualDays();
    BigDecimal depositIntervalMax = coefficients.getDepositIntervalMax();
    BigDecimal intervalScore = depositIntervalMax.add(depositIntervalManualDays.subtract(depositInterval).multiply(BIG_DECIMAL_HALF));
    if (intervalScore.compareTo(BigDecimal.ZERO) <0 ) {
      intervalScore = BigDecimal.ZERO;
    }
    if(intervalScore.compareTo(depositIntervalMax)>0) {
      intervalScore = depositIntervalMax;
    }
    totalScore = totalScore.add(intervalScore);

    // Total lifetime sales
    LocalDateTime oldDate = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    BigDecimal lifetimeSales = userSubscriptionRepository.getResellerTotalSale(reseller, oldDate);
    BigDecimal lifetimeTotalSales = userSubscriptionRepository.getAllResellerTotalSale(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC));
    BigDecimal avgLifetimeSales = lifetimeTotalSales.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal totalSalePercent = coefficients.getTotalSalePercent();
    BigDecimal totalSaleMax = coefficients.getTotalSaleMax();
    BigDecimal lifetimeTotalScore = calculateScore(lifetimeSales, avgLifetimeSales, totalSalePercent, totalSaleMax);
    totalScore = totalScore.add(lifetimeTotalScore);

    // Total month sales
    BigDecimal monthSales = userSubscriptionRepository.getResellerTotalSale(reseller, lastSetDate);
    BigDecimal monthTotalSales = userSubscriptionRepository.getAllResellerTotalSale(lastSetDate);
    BigDecimal avgMonthSales = monthTotalSales.divide(resellersCount, RoundingMode.HALF_UP);
    BigDecimal monthSalePercent = coefficients.getMonthSalePercent();
    BigDecimal monthSaleMax = coefficients.getMonthSaleMax();
    BigDecimal monthSaleScore = calculateScore(monthSales, avgMonthSales, monthSalePercent, monthSaleMax);
    totalScore = totalScore.add(monthSaleScore);

    List<ResellerLevel> allLevels = resellerLevelRepository.findAll();
    allLevels.sort(Comparator.comparing(ResellerLevel::getMinScore));
    ResellerLevel nextLevel = allLevels.get(0);
    // Skip ResellerLevel.OWNER
    for (int i = 0; i < allLevels.size() - 1; ++i) {
      ResellerLevel curLevel = allLevels.get(i);
      if (totalScore.compareTo(curLevel.getMinScore()) > 0) {
        nextLevel = curLevel;
      } else {
        break;
      }
    }

    return nextLevel;
  }

  public BigDecimal calculateScore(BigDecimal value, BigDecimal avgValue, BigDecimal coefPercent,
    BigDecimal max) {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal score = value.divide(avgValue, RoundingMode.HALF_UP).multiply(max).multiply(coefPercent).divide(BIG_DECIMAL_100, RoundingMode.HALF_UP);
    if (score.compareTo(max) > 0) {
      return max;
    }

    return score;
  }

}
