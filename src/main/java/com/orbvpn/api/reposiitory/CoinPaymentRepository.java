package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.CoinPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinPaymentRepository extends JpaRepository<CoinPayment, Long> {
}
