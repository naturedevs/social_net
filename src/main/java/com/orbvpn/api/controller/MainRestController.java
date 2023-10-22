package com.orbvpn.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbvpn.api.domain.dto.FacebookDeleteRequest;
import com.orbvpn.api.domain.dto.FacebookDeleteRequestResponse;
import com.orbvpn.api.domain.entity.OauthDeletedUser;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.reposiitory.OauthDeletedUserRepository;
import com.orbvpn.api.service.payment.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MainRestController {

  @Value("${stripe.api.webhook-secret}")
  private String STRIPE_SECRET_KEY;
  @Value("${oauth.facebook.app-secret}")
  private String FACEBOOK_SECRET;
  @Value("${application.website-url}")
  private String WEBSITE_URL;

  private final OauthDeletedUserRepository oauthDeletedUserRepository;

  private final PaymentService paymentService;

  @PostMapping("/appstore/events")
  public ResponseEntity<?> handleAppStoreEvent(HttpServletRequest httpServletRequest) {

    return ResponseEntity.ok().build();
  }

  public static String encode(String key, String data) throws Exception {
    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
    sha256_HMAC.init(secret_key);

    return Base64.encodeBase64URLSafeString(sha256_HMAC.doFinal(data.getBytes()));
  }

  @PostMapping("/stripe/events")
  public ResponseEntity<?> handleWebHook(@RequestBody String payload,
    @RequestHeader("Stripe-Signature") String sigHeader) {
    if (sigHeader == null) {
      return ResponseEntity.badRequest().build();
    }

    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, STRIPE_SECRET_KEY);
    } catch (SignatureVerificationException e) {
      e.printStackTrace();
      return ResponseEntity.badRequest().build();
    }

    EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
    StripeObject stripeObject;
    if (dataObjectDeserializer.getObject().isPresent()) {
      stripeObject = dataObjectDeserializer.getObject().get();
    } else {
      // Deserialization failed, probably due to an API version mismatch.
      // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
      // instructions on how to handle this case, or return an error here.
      return ResponseEntity.badRequest().build();
    }

    // Handle the event
    switch (event.getType()) {
      case "payment_intent.succeeded":
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        log.info("Payment for " + paymentIntent.getAmount() + " succeeded.");
        // Then define and call a method to handle the successful payment intent.
        paymentService.fullFillPayment(GatewayName.STRIPE, paymentIntent.getId());
        break;
      default:
        System.out.println("Unhandled event type: " + event.getType());
        break;
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/facebook-delete/confirmation")
  public ResponseEntity<?> confirmFacebookDelete(@RequestParam int id) {

    Optional<OauthDeletedUser> deletedUser = oauthDeletedUserRepository.findById(id);
    if (deletedUser.isPresent()) {
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.notFound().build();
  }

  @GetMapping("/facebook-delete")
  public ResponseEntity<?> handleFacebookDelete(HttpServletRequest request) {
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/facebook-delete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> handleFacebookDeletePost(
    @RequestParam("signed_request") String signedRequest)
    throws Exception {

    String[] split = signedRequest.split("\\.");
    String encodedSig = split[0];
    String encodedPayload = split[1];

    byte[] payloadBytes = Base64.decodeBase64(encodedPayload);

    String expected = encode(FACEBOOK_SECRET, encodedPayload);

    if (!expected.equals(encodedSig)) {
      throw new BadRequestException("Wrong signature Provided");
    }

    ObjectMapper objectMapper = new ObjectMapper();
    FacebookDeleteRequest facebookDeleteRequest = objectMapper
      .readValue(payloadBytes, FacebookDeleteRequest.class);

    OauthDeletedUser deletedUser = new OauthDeletedUser();
    deletedUser.setOauthId(facebookDeleteRequest.getUserId());
    oauthDeletedUserRepository.save(deletedUser);

    int id = deletedUser.getId();
    FacebookDeleteRequestResponse requestResponse = new FacebookDeleteRequestResponse();
    requestResponse.setUrl(MessageFormat.format("{0}/facebook-delete/confirmation/?id={1}", WEBSITE_URL, id));
    requestResponse.setConfirmationCode(String.valueOf(id));

    return ResponseEntity.ok(requestResponse);
  }
}
