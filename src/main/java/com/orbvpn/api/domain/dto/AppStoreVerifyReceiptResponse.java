package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType.SignatureRelevant;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppStoreVerifyReceiptResponse {
  private String environment;

  @JsonProperty("latest_receipt_info")
  private List<LatestReceiptInfo> latestReceiptInfo;

  @Getter
  @SignatureRelevant
  public static class LatestReceiptInfo {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("expires_date_ms")
    private String expiresDateMs;

    @JsonProperty("original_transaction_id")
    private String originalTransactionId;
  }
}
