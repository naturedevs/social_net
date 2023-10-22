package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.ResellerScoreLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResellerScoreLimitRepository extends JpaRepository<ResellerScoreLimit, Integer> {

    @Query(value = "SELECT * FROM reseller_score_limit where symbol=:symbol", nativeQuery=true)
    Optional<ResellerScoreLimit> findOneBySymbol(String symbol);
}
