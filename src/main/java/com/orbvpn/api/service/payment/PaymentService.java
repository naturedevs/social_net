package com.orbvpn.api.service.payment;

import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.payload.CoinPayment.AddressResponse;
import com.orbvpn.api.domain.payload.CoinPayment.CoinPaymentResponse;
import com.orbvpn.api.exception.PaymentException;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.service.*;
import com.orbvpn.api.service.payment.coinpayment.CoinPaymentService;
import com.orbvpn.api.utils.ThirdAPIUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {

  private final UserSubscriptionService userSubscriptionService;
  private final MoreLoginCountService moreLoginCountService;
  private final StripeService stripeService;
  private final PaypalService paypalService;
  private final CoinPaymentService coinpaymentService;
  private final AppleService appleService;
  private final RadiusService radiusService;
  private final GroupService groupService;
  private final ParspalService parspalService;
  private final InvoiceService invoiceService;
  private final ThirdAPIUtils apiUtil;

  @Setter
  private UserService userService;

  private final PaymentRepository paymentRepository;

  public void deleteUserPayments(User user) {
    paymentRepository.deleteByUser(user);
  }

  public void fullFillPayment(GatewayName gateway, String paymentId) {
    Payment payment = paymentRepository
      .findByGatewayAndPaymentId(gateway, paymentId)
      .orElseThrow(() -> new RuntimeException("Payment not found"));

    fullFillPayment(payment);
  }

  public void fullFillPayment(Payment payment) {
    if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
      throw new PaymentException("Payment is already fulfilled");
    }

    if (payment.getCategory() == PaymentCategory.GROUP) {
      Group group = groupService.getGroupIgnoreDelete(payment.getGroupId());

      // For apple we are using expiresAt from api
      if(payment.getExpiresAt() == null) {
        payment.setExpiresAt(LocalDateTime.now().plusDays(group.getDuration()));
      }

      userSubscriptionService.createUserSubscription(payment);
    } else if (payment.getCategory() == PaymentCategory.MORE_LOGIN) {
      User user = payment.getUser();
      UserSubscription userSubscription = userSubscriptionService.getCurrentSubscription(user);
      LocalDateTime expiresAt = userSubscription.getExpiresAt();

      int multiLoginCount = userSubscription.getMultiLoginCount() + payment.getMoreLoginCount();
      userSubscription.setMultiLoginCount(multiLoginCount);
      userSubscriptionService.save(userSubscription);
      radiusService.addUserMoreLoginCount(user, multiLoginCount);
      payment.setExpiresAt(expiresAt);

      MoreLoginCount moreLoginCountEntity = new MoreLoginCount();
      moreLoginCountEntity.setUser(user);
      moreLoginCountEntity.setExpiresAt(expiresAt);
      moreLoginCountEntity.setNumber(payment.getMoreLoginCount());
      moreLoginCountService.save(moreLoginCountEntity);
    }

    payment.setStatus(PaymentStatus.SUCCEEDED);
    paymentRepository.save(payment);
  }


    // TODO Fix payment issue
  public Payment renewPayment(Payment payment) throws Exception {
    Payment newPayment = Payment.builder()
      .user(payment.getUser())
      .status(PaymentStatus.PENDING)
      .gateway(payment.getGateway())
      .category(payment.getCategory())
      .price(payment.getPrice())
      .groupId(payment.getGroupId())
      .renew(true)
      .renewed(true)
      .build();

    if (payment.getGateway() == GatewayName.STRIPE) {
      PaymentIntent paymentIntent = stripeService.renewStripePayment(newPayment);
      newPayment.setPaymentId(paymentIntent.getId());
    } else if(payment.getGateway() == GatewayName.APPLE) {
      AppleSubscriptionData subscriptionData = appleService
        .getSubscriptionData(payment.getMetaData());
      payment.setPaymentId(subscriptionData.getOriginalTransactionId());
      payment.setExpiresAt(subscriptionData.getExpiresAt());
      payment.setMetaData(payment.getMetaData());
    }

    paymentRepository.save(newPayment);
    fullFillPayment(newPayment);
    return newPayment;
  }

  public StripePaymentResponse stripeCreatePayment(PaymentCategory category, int groupId,
                                                   int moreLoginCount, boolean renew, String paymentMethodId)
    throws StripeException {

    Payment payment = createPayment(GatewayName.STRIPE, category, groupId, moreLoginCount, renew);
    User user = userService.getUser();
    return stripeService.createStripePayment(payment, user, paymentMethodId);
  }

  public PaypalCreatePaymentResponse paypalCreatePayment(PaymentCategory category, int groupId, int moreLoginCount) throws Exception {
    Payment payment = createPayment(GatewayName.PAYPAL, category, groupId, moreLoginCount, false);
    return paypalService.createPayment(payment);
  }


  public CoinPaymentResponse coinpaymentCreatePayment(PaymentCategory category, int groupId, int moreLoginCount,
                                                      String coin) throws Exception {
    Payment payment = createPayment(GatewayName.COIN_PAYMENT, category, groupId, moreLoginCount, false);
    User user = userService.getUser();
    
    // v1
    CoinPayment coinPayment = CoinPayment.builder()
      .user(user)
      .payment(payment)
      .coin(coin)
      .build();

      
      return coinpaymentService.createPayment(coinPayment);
    }
    
    public AddressResponse coinpaymentCreatePaymentV2(PaymentCategory category, int groupId, int moreLoginCount, String coin) throws IOException {
      Payment payment = createPayment(GatewayName.COIN_PAYMENT, category, groupId, moreLoginCount, false);
      var user = userService.getUser();
      
      // Get price
      var usdPrice = payment.getPrice();
      var cryptoName = coin.contains(".") ? coin.split(".")[0] : coin;
      var cryptoPrice = apiUtil.getCryptoPriceBySymbol(cryptoName);
      var cryptoAmount = usdPrice.doubleValue() / cryptoPrice;

      // v2 using Callback Address
      var coinPaymentCallback = CoinPaymentCallback.builder() 
        .user(user)
        .payment(payment)
        .coin(coin)
        .coinAmount(cryptoAmount)
        .build();
      return coinpaymentService.createPayment(coinPaymentCallback);
  }

  public boolean appleCreatePayment(String receipt) {
    log.info("Creating payment for apple");

    AppleSubscriptionData appleSubscriptionData = appleService.getSubscriptionData(receipt);
    Payment payment = createPayment(GatewayName.APPLE, PaymentCategory.GROUP, appleSubscriptionData.getGroupId(), 0, true);
    payment.setPaymentId(appleSubscriptionData.getOriginalTransactionId());
    payment.setMetaData(receipt);
    payment.setExpiresAt(appleSubscriptionData.getExpiresAt());
    fullFillPayment(payment);


    log.info("Created payment for apple receipt : {}", payment);

    return true;
  }

  public PaypalApprovePaymentResponse paypalApprovePayment(String orderId) {
    PaypalApprovePaymentResponse approveResponse = paypalService.approvePayment(orderId);
    if (approveResponse.isSuccess()) {
      fullFillPayment(GatewayName.PAYPAL, orderId);
    }

    return approveResponse;
  }

  public ParspalCreatePaymentResponse parspalCreatePayment(PaymentCategory category, int groupId,
    int moreLoginCount) {
    Payment payment = createPayment(GatewayName.PARSPAL, category, groupId, moreLoginCount, false);
    return parspalService.createPayment(payment);
  }

  public boolean parspalApprovePayment(String payment_id, String receipt_number) {
    boolean approved = parspalService.approvePayment(payment_id, receipt_number);
    if (approved) {
      fullFillPayment(GatewayName.PARSPAL, payment_id);
    }
    return approved;
  }

  public Payment createPayment(GatewayName gateway, PaymentCategory category, int groupId,
    int moreLoginCount, boolean renew) {
    if (category == PaymentCategory.GROUP) {
      return createGroupPayment(groupId, renew, gateway);
    }

    if (category == PaymentCategory.MORE_LOGIN) {
      return createBuyMoreLoginPayment(moreLoginCount, gateway);
    }

    if (category == PaymentCategory.BUY_CREDIT) {
      return createBuyCreditPayment(moreLoginCount, gateway);
    }

    throw new PaymentException("Not supported category");
  }

  public Payment createGroupPayment(int groupId, boolean renew, GatewayName gateway) {
    User user = userService.getUser();
    Group group = groupService.getById(groupId);

    Payment payment = Payment.builder()
      .user(user)
      .status(PaymentStatus.PENDING)
      .gateway(gateway)
      .category(PaymentCategory.GROUP)
      .moreLoginCount(group.getMultiLoginCount())
      .price(group.getPrice())
      .groupId(groupId)
      .renew(renew)
      .build();

    payment = paymentRepository.save(payment);
    invoiceService.createInvoice(payment);

    return payment;
  }

  public Payment createBuyMoreLoginPayment(int number, GatewayName gateway) {
    User user = userService.getUser();

    Payment payment = Payment.builder()
      .user(user)
      .status(PaymentStatus.PENDING)
      .gateway(gateway)
      .category(PaymentCategory.MORE_LOGIN)
      .price(getBuyMoreLoginPrice(number))
      .moreLoginCount(number)
      .build();

    invoiceService.createInvoice(payment);
    paymentRepository.save(payment);
    return payment;
  }

  public Payment createBuyCreditPayment(int number, GatewayName gateway){
    User user = userService.getUser();

    Payment payment = Payment.builder()
            .user(user)
            .status(PaymentStatus.PENDING)
            .gateway(gateway)
            .category(PaymentCategory.BUY_CREDIT)
            .price(getCreditPrice(number))
            .moreLoginCount(number)
            .build();

    invoiceService.createInvoice(payment);
    paymentRepository.save(payment);
    return payment;
  }

  public BigDecimal getCreditPrice(int number) {
    return new BigDecimal(number);
  }

  public BigDecimal getBuyMoreLoginPrice(int number) {
    LocalDateTime now = LocalDateTime.now();
    User user = userService.getUser();
    UserSubscription subscription = userSubscriptionService.getCurrentSubscription(user);
    Group group = subscription.getGroup();
    ServiceGroup serviceGroup = group.getServiceGroup();

    BigDecimal groupPrice = group.getPrice();

    BigDecimal serviceGroupDiscountMultiplier = BigDecimal.ONE
      .subtract(serviceGroup.getDiscount().divide(new BigDecimal(100),RoundingMode.HALF_UP));

    LocalDateTime expiresAt = subscription.getExpiresAt();
    BigDecimal expirationDays  = new BigDecimal(DAYS.between(now, expiresAt));

    BigDecimal accountDays = new BigDecimal(DAYS.between(user.getCreatedAt(), now));

   return new BigDecimal(number).multiply(groupPrice)
           .multiply(serviceGroupDiscountMultiplier).multiply(expirationDays)
           .divide(accountDays, RoundingMode.UP);
  }

  public List<Payment> findAllSubscriptionPaymentsToRenew() {

    List<Payment> paymentsToRenew = paymentRepository.findAllSubscriptionPaymentsToRenew(LocalDateTime.now());
    Iterator<Payment> paymentIterator = paymentsToRenew.iterator();

    while (paymentIterator.hasNext()) {
      Payment payment = paymentIterator.next();

      UserSubscription currentSubscription = userSubscriptionService.getCurrentSubscription(payment.getUser());
      if (currentSubscription.getPayment() != payment)
        paymentIterator.remove();
    }

     return paymentsToRenew;
  }

}
