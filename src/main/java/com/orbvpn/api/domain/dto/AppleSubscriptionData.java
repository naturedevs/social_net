package com.orbvpn.api.domain.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleSubscriptionData {
  private int groupId;
  private String receipt;
  private LocalDateTime expiresAt;
  private String originalTransactionId;
}
