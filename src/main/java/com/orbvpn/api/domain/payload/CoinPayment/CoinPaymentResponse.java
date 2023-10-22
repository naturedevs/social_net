package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CoinPaymentResponse {

    private long id;
    private String error;
    private String address;
    private String amount;
    private String txn_id;
    private String confirms_needed;
    private Integer timeout;
    private String checkout_url;
    private String status_url;
    private String qrcode_url;
}
