package com.orbvpn.api.service.payment;

import com.orbvpn.api.domain.dto.StripePaymentResponse;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.StripeCustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentCreateParams.Builder;
import com.stripe.param.PaymentMethodListParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

  private final double STRIPE_FEE = 2.9;

  @Value("${stripe.api.public-key}")
  private String STRIPE_PUBLIC_KEY;
  @Value("${stripe.api.secret-key}")
  private String STRIPE_SECRET_KEY;

  @PostConstruct
  public void init() {
    Stripe.apiKey = STRIPE_SECRET_KEY;
  }

  private final StripeCustomerRepository stripeCustomerRepository;
  private final PaymentRepository paymentRepository;

  public StripePaymentResponse createStripePayment(Payment payment, User user, String stripeMethodId) throws StripeException {

    StripeCustomer stripeCustomer = stripeCustomerRepository.findByUser(user);

    if(stripeCustomer == null) {
      CustomerCreateParams params =
        CustomerCreateParams.builder()
          .build();

      Customer customer = Customer.create(params);
      stripeCustomer = new StripeCustomer();
      stripeCustomer.setUser(user);
      stripeCustomer.setStripeId(customer.getId());
      stripeCustomerRepository.save(stripeCustomer);
    }

    BigDecimal amount = payment.getPrice().multiply(new BigDecimal(100));
    Builder createParams = new Builder()
      .setCurrency("USD")
      .setAmount(amount.longValue())
      .setPaymentMethod(stripeMethodId)
      .setConfirm(true)
      .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL);

    if (payment.isRenew()) {
      createParams.setCustomer(stripeCustomer.getStripeId());
      createParams.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
    }

    PaymentIntent intent = PaymentIntent.create(createParams.build());
    payment.setPaymentId(intent.getId());

    return generateResponse(intent, payment);
  }

  public PaymentIntent renewStripePayment(Payment payment) throws Exception {
    User user = payment.getUser();
    BigDecimal price = payment.getPrice();

    StripeCustomer stripeCustomer = stripeCustomerRepository.findByUser(user);
    BigDecimal priceInCents = price.multiply(new BigDecimal(100));
    String customerId =  stripeCustomer.getStripeId();

    PaymentMethodListParams custParams =
      PaymentMethodListParams.builder()
        .setCustomer(customerId)
        .setType(PaymentMethodListParams.Type.CARD)
        .build();

    PaymentMethodCollection paymentMethods = PaymentMethod.list(custParams);

    String paymentId = paymentMethods.getData().get(0).getId();

    PaymentIntentCreateParams params =
      PaymentIntentCreateParams.builder()
        .setCurrency("usd")
        .setAmount(priceInCents.longValue())
        .setCustomer(customerId)
        .setPaymentMethod(paymentId)
        .setConfirm(true)
        .setOffSession(true)
        .build();

    return PaymentIntent.create(params);
  }

  public double getStripeFee(int userId, double amount) {
    return  (amount * STRIPE_FEE / 100) + 30;
  }

  public double getTotalAmount(int userId, double amount) {
    double fee = getStripeFee(userId, amount);
    return amount + fee;
  }

  protected StripePaymentResponse generateResponse(PaymentIntent intent,Payment payment) {

    StripePaymentResponse response = new StripePaymentResponse();
    if(intent == null) {
      response.setError("Unrecognized status");
      payment.setStatus(PaymentStatus.FAILED);
      return response;
    }
    switch (intent.getStatus()) {
      case "requires_action":
        response.setClientSecret(intent.getClientSecret());
        response.setRequiresAction(true);
        break;
      case "requires_source_action":
        // Card requires authentication
        response.setClientSecret(intent.getClientSecret());
        response.setPaymentIntentId(intent.getId());
        response.setRequiresAction(true);
        break;
      case "requires_payment_method":
        response.setError("requires_payment_method");
        payment.setStatus(PaymentStatus.FAILED);
        break;
      case "requires_capture":
        response.setRequiresAction(false);
        response.setClientSecret(intent.getClientSecret());
        break;
      case "requires_source":
        // Card was not properly authenticated, suggest a new payment method
        response.setError("Your card was denied, please provide a new payment method");
        payment.setStatus(PaymentStatus.FAILED);
        break;
      case "succeeded":
        log.info("💰 Payment received!");
        // Payment is complete, authentication not required
        // To cancel the payment after capture you will need to issue a Refund
        // (https://stripe.com/docs/api/refunds)
        response.setClientSecret(intent.getClientSecret());
        response.setPaymentIntentId(intent.getId());
        payment.setStatus(PaymentStatus.SUCCEEDED);
        break;
      default:
        response.setError("Unrecognized status");
        payment.setStatus(PaymentStatus.FAILED);
    }
    paymentRepository.save(payment);
    return response;
  }
}
