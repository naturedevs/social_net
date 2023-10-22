package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Integer> {
    ReferralCode findReferralCodeByCode(String code);
}
