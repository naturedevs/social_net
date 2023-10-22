package com.orbvpn.api.service.payment;

import com.orbvpn.api.domain.dto.ParspalApprovePaymentRequest;
import com.orbvpn.api.domain.dto.ParspalApprovePaymentResponse;
import com.orbvpn.api.domain.dto.ParspalCreatePaymentRequest;
import com.orbvpn.api.domain.dto.ParspalCreatePaymentResponse;
import com.orbvpn.api.domain.entity.Payment;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ParspalService {

  @Value("${parspal.url}")
  private String apiUrl;
  @Value("${parspal.api-key}")
  private String apiKey;
  @Value("${parspal.return-url}")
  private String returnUrl;

  private final PaymentRepository paymentRepository;

  public ParspalCreatePaymentResponse createPayment(Payment payment) {

    ParspalCreatePaymentRequest requestBody = new ParspalCreatePaymentRequest();

    requestBody.setAmount(payment.getPrice().doubleValue());
    requestBody.setReturnUrl(returnUrl);
    requestBody.setOrderId(payment.getPaymentId());

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("APIKey", apiKey);
    HttpEntity<ParspalCreatePaymentRequest> request = new HttpEntity<>(requestBody, headers);

    ResponseEntity<ParspalCreatePaymentResponse> result =
      restTemplate.postForEntity(apiUrl + "/payment/request", request, ParspalCreatePaymentResponse.class);

    ParspalCreatePaymentResponse body = result.getBody();

    payment.setPaymentId(body.getPayment_id());
    paymentRepository.save(payment);

    return body;
  }

  public boolean approvePayment(String paymentId, String receipt) {
    Payment payment = paymentRepository
      .findByGatewayAndPaymentId(GatewayName.PARSPAL, paymentId)
      .orElseThrow(()->new NotFoundException("Can not find payment"));

    ParspalApprovePaymentRequest parspalApprovePaymentRequest = new ParspalApprovePaymentRequest();
    parspalApprovePaymentRequest.setAmount(payment.getPrice().doubleValue());
    parspalApprovePaymentRequest.setReceipt(receipt);

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("APIKey", apiKey);
    HttpEntity<ParspalApprovePaymentRequest> request = new HttpEntity<>(parspalApprovePaymentRequest, headers);

    ResponseEntity<ParspalApprovePaymentResponse> parspalResponseEntity =
      restTemplate.postForEntity(apiUrl + "/payment/verify", request, ParspalApprovePaymentResponse.class);

    ParspalApprovePaymentResponse approvePaymentResponse = parspalResponseEntity.getBody();

    return approvePaymentResponse.getStatus().equals("100");
  }
}
