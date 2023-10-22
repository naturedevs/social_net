package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoinPaymentsRateRequest {

    private boolean onlyAccepted = true;
    private boolean onlyShort = true;

    @Override
    public String toString() {
        return "cmd=rates&accepted=" + (onlyAccepted ? "2" : "0") + "&short=" + (onlyShort ? "1" : "0");
    }
}
