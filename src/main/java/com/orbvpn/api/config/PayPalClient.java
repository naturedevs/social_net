package com.orbvpn.api.config;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PayPalClient {

  /**
   * Set up the PayPal Java SDK environment with PayPal access credentials. This sample uses
   * SandboxEnvironment. In production, use LiveEnvironment.
   */
  @Value("${paypal.client-id}")
  private String clientId;
  @Value("${paypal.secret}")
  private String clientSecret;
  @Value("${paypal.mode}")
  private String mode;

  /**
   * Method to get client object
   *
   * @return PayPalHttpClient client
   */
  public PayPalHttpClient client() {
    PayPalEnvironment environment;

    if (mode.equals("sandbox")) {
      environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
    } else {
      environment = new PayPalEnvironment.Live(clientId, clientSecret);
    }

    /*
     *PayPal HTTP client instance with environment that has access
     *credentials context. Use to invoke PayPal APIs.
     */
    PayPalHttpClient client = new PayPalHttpClient(environment);
    client.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(900));
    return client;
  }

}