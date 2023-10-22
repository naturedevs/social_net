package com.orbvpn.api.exception;

public class CouponCodeExpiredException extends RuntimeException {
    public CouponCodeExpiredException(String code) {
        super(String.format("Coupon code %s has been expired.",code));
    }
}
