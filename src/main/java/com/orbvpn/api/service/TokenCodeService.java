package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.TokenCodeDto;
import com.orbvpn.api.domain.dto.TokenCodeResponse;
import com.orbvpn.api.domain.dto.TokenCodeView;
import com.orbvpn.api.domain.entity.TokenCode;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.exception.TokenCodeAlreadyUsedException;
import com.orbvpn.api.exception.UnauthenticatedAccessException;
import com.orbvpn.api.mapper.TokenCodeMapper;
import com.orbvpn.api.reposiitory.TokenCodeRepository;
import com.orbvpn.api.service.notification.NotificationService;
import com.orbvpn.api.utils.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenCodeService {

    private final TokenCodeRepository tokenCodeRepository;
    private final NotificationService notificationService;
    private final TokenCodeMapper tokenCodeMapper;
    private final UserService userService;

    public TokenCodeResponse generateTokenCodeForUser(TokenCodeDto tokenCodeDto) {

        User user = userService.getUserById(tokenCodeDto.getUserId());

        TokenCode tokenCode = new TokenCode();
        tokenCode.setUser(user);
        tokenCode.setDiscountRate(tokenCodeDto.getDiscountRate());
        tokenCode.setTokenCode(Utilities.getRandomUpperCaseString(10));
        tokenCodeRepository.save(tokenCode);

        notificationService.sendTokenCodeToUser(user, tokenCode.getTokenCode());

        return new TokenCodeResponse(tokenCode.getTokenCode(), "Token code created.");
    }

    public TokenCodeView useTokenCode(String code) {

        TokenCode tokenCode = tokenCodeRepository.findByTokenCode(code).
                orElseThrow(() -> new NotFoundException(TokenCode.class, code));

        if(userService.getUser() != tokenCode.getUser())
            throw new UnauthenticatedAccessException("You are not authorized to use this token!");

        if(!tokenCode.isActive())
            throw new TokenCodeAlreadyUsedException(code);

        tokenCode.setActive(false);
        tokenCode.setUsedTimestamp(LocalDateTime.now());
        tokenCodeRepository.save(tokenCode);

        TokenCodeView tokenCodeView = tokenCodeMapper.toTokenCodeView(tokenCode);
        tokenCodeView.setUserId(tokenCode.getUser().getId());
        return tokenCodeView;
    }

    public TokenCodeView checkTokenCode(String code) {

        TokenCode tokenCode = tokenCodeRepository.findByTokenCode(code).
                orElseThrow(() -> new NotFoundException(TokenCode.class, code));

        if(!tokenCode.isActive()) {
            throw new TokenCodeAlreadyUsedException(code);
        }

        TokenCodeView tokenCodeView = tokenCodeMapper.toTokenCodeView(tokenCode);
        tokenCodeView.setUserId(tokenCode.getUser().getId());

        return tokenCodeView;
    }

}
