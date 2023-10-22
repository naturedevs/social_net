package com.orbvpn.api.resolver.mutation;

import java.io.IOException;

import com.orbvpn.api.domain.dto.ParspalCreatePaymentResponse;
import com.orbvpn.api.domain.dto.PaypalApprovePaymentResponse;
import com.orbvpn.api.domain.dto.PaypalCreatePaymentResponse;
import com.orbvpn.api.domain.dto.StripePaymentResponse;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.payload.CoinPayment.AddressResponse;
import com.orbvpn.api.domain.payload.CoinPayment.CoinPaymentResponse;
import com.orbvpn.api.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class PaymentMutation implements GraphQLMutationResolver {

  private final PaymentService paymentService;

  public StripePaymentResponse stripeCreatePayment(PaymentCategory category, int groupId,
                                                   int moreLoginCount, boolean renew, String paymentMethodId)
    throws StripeException {
    return paymentService.stripeCreatePayment(category, groupId, moreLoginCount, renew, paymentMethodId);
  }

  /**
   * 
   * @param category GROUP or More Login
   * @param groupId ??
   * @param moreLoginCount ??
   * @param coin target coin => must be 'BTC.BEP20' format
   * @return Coinpayment url will be returned including Deposit address and qrcode image??
   * @throws Exception
   */
  public CoinPaymentResponse coinpaymentCreatePayment(PaymentCategory category, int groupId, int moreLoginCount,
                                                      String coin) throws Exception {
    return paymentService.coinpaymentCreatePayment(category, groupId, moreLoginCount,coin);
  }

  public AddressResponse coinpaymentCreatePayment2(PaymentCategory category, int groupId, int moreLoginCount, String coin) throws IOException {
    return paymentService.coinpaymentCreatePaymentV2(category, groupId, moreLoginCount, coin);
  }

  public PaypalCreatePaymentResponse paypalCreatePayment(PaymentCategory category, int groupId,
    int moreLoginCount)
    throws Exception {
    return paymentService.paypalCreatePayment(category, groupId, moreLoginCount);
  }

  public PaypalApprovePaymentResponse paypalApprovePayment(String orderId) {
    return paymentService.paypalApprovePayment(orderId);
  }


  public ParspalCreatePaymentResponse parspalCreatePayment(PaymentCategory category, int groupId,
    int moreLoginCount) {
    return paymentService.parspalCreatePayment(category, groupId, moreLoginCount);
  }

  public boolean parspalApprovePayment(String payment_id, String receipt_number) {
    return paymentService.parspalApprovePayment(payment_id, receipt_number);
  }

  public boolean appleCreatePayment(String receipt) {
    return paymentService.appleCreatePayment(receipt);
  }

}
