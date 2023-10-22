package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoinPaymentsGetCallbackRequest {
    private String currency;
    private String IPNUrl;

    @Override
    public String toString() {
        return "cmd=get_callback_address" + "&currency=" + currency + "&ipn_url=" + IPNUrl;
    }
}
