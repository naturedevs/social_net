package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.CouponCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponCodeRepository extends JpaRepository<CouponCode, Integer> {

    Optional<CouponCode> findByCouponCode(String couponCode);
}
