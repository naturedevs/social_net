package com.orbvpn.api.service.payment.coinpayment;

import com.google.gson.Gson;
import com.orbvpn.api.domain.payload.CoinPayment.CoinPaymentsRateRequest;
import lombok.Getter;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.orbvpn.api.service.payment.coinpayment.Constants.COINS_API_URL;
import static com.orbvpn.api.service.payment.coinpayment.Constants.HMAC_SHA_512;

@Getter
public class CoinPaymentBaseService {

    @Value("${coinpayment.key.public}")
    protected String publicKey;

    @Value("${coinpayment.key.private}")
    protected String privateKey;

    @Value("${coinpayment.ipn.secret}")
    protected String ipnSecret;

    @Value("${coinpayment.ipn.url}")
    protected String IPN_URL;

    @Value("${coinpayment.merchant-id}")
    protected String merchantId;

    protected CloseableHttpClient client;
    protected static Gson gson;

    @Autowired
    public CoinPaymentBaseService() {
        client = HttpClients.createDefault();
        gson = new Gson();
    }

    public String getExchangeRate() throws ParseException, IOException {
        HttpPost post = new HttpPost(COINS_API_URL);
        post.addHeader("Connection", "close");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.addHeader("Cookie2", "$Version=1");
        post.addHeader("Accept-Language", "en-US");

        CoinPaymentsRateRequest request = new CoinPaymentsRateRequest();
        String payload = request.toString();
        payload += "&version=1&key=" + publicKey + "&format-json";
        String hmac = buildHmacSignature(payload, privateKey);

        post.addHeader("HMAC", hmac);
        post.setEntity(new StringEntity(payload));
        CloseableHttpResponse response = client.execute(post);

        return EntityUtils.toString(response.getEntity());
    }

    public static String buildHmacSignature(String value, String secret) {
        String result;
        try {
            Mac hmacSHA512 = Mac.getInstance(HMAC_SHA_512);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA_512);
            hmacSHA512.init(secretKeySpec);

            byte[] digest = hmacSHA512.doFinal(value.getBytes());
            BigInteger hash = new BigInteger(1, digest);
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (IllegalStateException | InvalidKeyException | NoSuchAlgorithmException ex) {
            throw new RuntimeException("Problem calculating HMAC", ex);
        }
        return result;
    }
}

