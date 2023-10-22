package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.PromotionType;
import com.orbvpn.api.domain.entity.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionTypeRepository extends JpaRepository<PromotionType, Integer> {
    PromotionType findPromotionTypeByName(String name);
}
