package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.TokenCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenCodeRepository extends JpaRepository<TokenCode, Integer> {

    Optional<TokenCode> findByTokenCode(String tokenCode);
}
