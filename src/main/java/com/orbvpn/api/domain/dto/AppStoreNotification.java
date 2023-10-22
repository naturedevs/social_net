package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppStoreNotification {
  private String auto_renew_adam_id;

  private String auto_renew_product_id;

  private String auto_renew_status;

  private String auto_renew_status_change_date;

  private String auto_renew_status_change_date_ms;

  private String auto_renew_status_change_date_pst;

  private String environment;

  private int expiration_intent;

  private String notification_type;

  private String password;

  private UnifiedReceipt unified_receipt;

  private String bid;

  private String bvrs;

  public static class UnifiedReceipt {

  }
}
