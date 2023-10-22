package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.CoinPaymentCallback;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinPaymentCallbackRepository extends JpaRepository<CoinPaymentCallback, Long> {
}
