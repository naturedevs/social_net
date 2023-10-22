package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptRequest;
import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptResponse;
import com.orbvpn.api.domain.dto.AppStoreVerifyReceiptResponse.LatestReceiptInfo;
import com.orbvpn.api.domain.dto.AppleSubscriptionData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AppleService {

  private final Map<String, Integer> groupMap = Map.of(
    "com.orb.monthly.basic", 9,
    "com.orb.monthly.premium", 10,
    "com.orb.monthly.familypremium", 11,
    "com.orb.yearly.basic", 12,
    "com.orb.yearly.premium", 13,
    "com.orb.yearly.familypremium", 14);
  @Value("${app-store.url}")
  private String APP_STORE_URL;
  @Value("${app-store.secret}")
  private String SECRET;

  public AppleSubscriptionData getSubscriptionData(String receipt) {
    AppStoreVerifyReceiptRequest verifyReceiptRequest = new AppStoreVerifyReceiptRequest();
    verifyReceiptRequest.setReceiptData(receipt);
    verifyReceiptRequest.setPassword(SECRET);

    RestTemplate restTemplate = new RestTemplate();

    AppStoreVerifyReceiptResponse response = restTemplate
      .postForObject(APP_STORE_URL + "/verifyReceipt", verifyReceiptRequest,
        AppStoreVerifyReceiptResponse.class);

    LatestReceiptInfo latestReceiptInfo = response.getLatestReceiptInfo().get(0);
    String productSku = latestReceiptInfo.getProductId();

    Integer groupId = groupMap.get(productSku);
    LocalDateTime expiresAt = Instant.ofEpochMilli(Long.parseLong(latestReceiptInfo.getExpiresDateMs()))
      .atZone(ZoneId.systemDefault()).toLocalDateTime();

    AppleSubscriptionData appleSubscriptionData = new AppleSubscriptionData();
    appleSubscriptionData.setGroupId(groupId);
    appleSubscriptionData.setExpiresAt(expiresAt);
    appleSubscriptionData.setReceipt(receipt);
    appleSubscriptionData.setOriginalTransactionId(latestReceiptInfo.getOriginalTransactionId());

    return appleSubscriptionData;

  }
}
