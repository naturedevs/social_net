package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResult {

    private String address;
    private String amount;
    private String txn_id;
    private String confirms_needed;
    private Integer timeout;
    private String checkout_url;
    private String status_url;
    private String qrcode_url;
}
