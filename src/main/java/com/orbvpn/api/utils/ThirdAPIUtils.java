package com.orbvpn.api.utils;

import com.google.gson.Gson;
import com.orbvpn.api.domain.payload.CoinPrice;
import com.orbvpn.api.domain.payload.FiatConverted;
import com.orbvpn.api.domain.payload.FreaksResponse;

import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ThirdAPIUtils {

    private WebClient binanceAPI;
    private WebClient xchangeAPI;

    private static Gson gson = new Gson();

    public ThirdAPIUtils (WebClient.Builder webClientBuilder) {
        this.binanceAPI = webClientBuilder
            .baseUrl("https://api.binance.com/api/v3")
            .build();
        this.xchangeAPI = webClientBuilder
            .baseUrl("https://api.exchangerate.host")
            .build();
    }

    public double getCryptoPriceBySymbol(String symbol) {
        String symbolPair = "";
        try {
            if(symbol.equals("USDC")) {
                return 1.0;
            } else if(symbol.equals("USDT")) {
                symbolPair = "USDCUSDT";
            } else {
                symbolPair = symbol + "USDC";
            }
            String s = symbolPair;
            CoinPrice objs = binanceAPI.get()
                    .uri(uriBuilder -> uriBuilder.path("/ticker/price")
                            .queryParam("symbol", s.toUpperCase())
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .onStatus(org.springframework.http.HttpStatus::is4xxClientError, response -> {
                        return null;
                    })
                    .bodyToMono(CoinPrice.class)
                    .onErrorMap(throwable -> {
                        return null;
                    })
                    .block();
            return Double.valueOf(objs.getPrice());
        } catch (Exception e) {
        }
        return 0.0;
    }

    public double currencyConvert(String from, String to, double amount) {
        try {
            String converted = xchangeAPI.get()
                .uri(uriBuilder -> uriBuilder.path("/convert")
                    .queryParam("from", from)
                    .queryParam("to", to)
                    .queryParam("amount", amount)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            FiatConverted fiatConverted = gson.fromJson(converted, FiatConverted.class);
            return fiatConverted.getResult();
        } catch (Exception e) {
        
        }
        return 0.0;
    }

    public double getCurrencyRate(String from) {
        try {
            String converted = xchangeAPI.get()
                .uri(uriBuilder -> uriBuilder.path("/latest")
                    .queryParam("symbols", from)
                    .queryParam("base", "USD")
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
            var rates = gson.fromJson(converted, FreaksResponse.class);
            String rateString = rates.getRates().get(from);
            return Double.parseDouble(rateString);
        } catch (Exception e) {
        
        }
        return 0.0;
    }
}
