package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPaymentsCreateTransactionRequest {

    private BigDecimal amount;
    private String currency;
    private String buyerEmail;

    @Value("${coinpayments.callbackUrl}")
    private String baseUrl;

    @Override
    public String toString() {
        return "cmd=create_transaction" + "&amount=" + amount.toString() + "&currency1=USD" + "&currency2=" + currency + "&buyer_email=" + buyerEmail + "&success_url=" + baseUrl + "/success" + "&cancel_url=" + baseUrl+ "/cancel";
    }
}
