package com.orbvpn.api.controller;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.orbvpn.api.exception.PaymentException;
import com.orbvpn.api.service.payment.PaymentService;
import com.orbvpn.api.service.payment.coinpayment.CoinPaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/")
public class CoinPaymentController {

    private static final String HMAC_SHA_512 = "HmacSHA512";

    @Value("${coinpayment.ipn.secret}")
    protected String COINSPAYMENT_IPN_SECRET;

    @Value("${coinpayment.merchant-id}")
    protected String MERCHANT_ID;

    private final CoinPaymentService coinPaymentService;
    private final PaymentService paymentService;


    @Autowired
    public CoinPaymentController(CoinPaymentService coinPaymentService,
                                 PaymentService paymentService) {
        this.coinPaymentService = coinPaymentService;
        this.paymentService = paymentService;
    }


    @PostMapping("/ipn/{id}")
    @ResponseBody
    public ResponseEntity<String> success(@PathVariable("id") Long id, HttpServletRequest request) {
        String ipnMode = request.getParameter("ipn_mode");
		if(!ipnMode.equals("hmac")) {
			log.error("IPN Mode is not HMAC");
			throw new PaymentException("IPN Mode is not HMAC");
		}
		
		String hmac = request.getHeader("hmac");
		if(hmac == null) {
			log.error("IPN Hmac is null");
			throw new PaymentException("IPN Hmac is null");
		}
		
		String merchant = request.getParameter("merchant");
		if(merchant == null || !merchant.equals(MERCHANT_ID)) {
			log.error("No or incorrect Merchant ID passed");
			throw new PaymentException("No or incorrect Merchant ID passed");
		}
		
		String reqQuery = "";
		Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()){
            String parameterName = (String) enumeration.nextElement();
            reqQuery += parameterName + "=" + request.getParameter(parameterName) + "&";
        }
		
		reqQuery = (String) reqQuery.subSequence(0, reqQuery.length() - 1);
		reqQuery = reqQuery.replaceAll("@", "%40");
		reqQuery = reqQuery.replace(' ', '+');
		log.info("IPN reqQuery : {}", reqQuery);
		
		String _hmac = buildHmacSignature(reqQuery, COINSPAYMENT_IPN_SECRET);
		if(!_hmac.equals(hmac)) {
            log.error("hmac doesn't match");
			throw new PaymentException("not match");
		}
        
        /// crypto paid amount
        Double amount = getDouble(request, "amount");
		
		int status = getInt(request, "status");
		log.info("IPN status : {}", status);

        if (status >= 100 || status == 2) {
            // get payment 
            var cryptoPayment = coinPaymentService.getCallbackPayment(id);
            var payment = cryptoPayment.getPayment();

            if(cryptoPayment.getCoinAmount() > amount) {
                return new ResponseEntity<>("Insufficient", HttpStatus.BAD_REQUEST);
            }
            cryptoPayment.setStatus(1);
            coinPaymentService.save(cryptoPayment);
            
            // fulfill payment
            paymentService.fullFillPayment(payment);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    public int getInt(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getString(HttpServletRequest request, String param, Boolean trim) {
        try {
            String result = request.getParameter(param);
            if(trim) {
                result = result.trim();
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public double getDouble(HttpServletRequest request, String param) {
        try {
            Object value = request.getParameter(param);
            if(value == null) {
                return 0;
            }
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String buildHmacSignature(String value, String secret) {
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
            throw new RuntimeException("Problemas calculando HMAC", ex);
        }
        return result;
    }
}
