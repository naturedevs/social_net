package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.CouponCodeDto;
import com.orbvpn.api.domain.entity.CouponCode;
import com.orbvpn.api.exception.CouponCodeExpiredException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.CouponCodeMapper;
import com.orbvpn.api.reposiitory.CouponCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponCodeService {

    private final CouponCodeRepository couponCodeRepository;
    private final CouponCodeMapper couponCodeMapper;

    public CouponCode createCouponCode(CouponCodeDto couponCodeDto) {

        couponCodeRepository.findByCouponCode(couponCodeDto.getCouponCode())
                .ifPresent(s -> {
                    throw new EntityExistsException(String.format("This coupon code already exists : %s.", couponCodeDto.getCouponCode()));
                });

        CouponCode code = couponCodeMapper.toCouponCode(couponCodeDto);
        couponCodeRepository.save(code);

        return code;
    }

    public CouponCode updateCouponCode(CouponCodeDto couponCodeDto) {
        CouponCode oldCode = couponCodeRepository.findByCouponCode(couponCodeDto.getCouponCode()).
                orElseThrow(() -> new NotFoundException(CouponCode.class, couponCodeDto.getCouponCode()));

        couponCodeRepository.delete(oldCode);
        CouponCode codeToUpdate = couponCodeMapper.toCouponCode(couponCodeDto);
        codeToUpdate.setCreatedAt(oldCode.getCreatedAt());
        couponCodeRepository.save(codeToUpdate);

        return codeToUpdate;
    }

    public CouponCode useCouponCode(String code) {

        CouponCode couponCode = couponCodeRepository.findByCouponCode(code).
                orElseThrow(() -> new NotFoundException(CouponCode.class, code));

        if(couponCode.isCodeValid()) {
            throw new CouponCodeExpiredException(code);
        }
        couponCode.decreaseQuantity();
        couponCodeRepository.save(couponCode);

        return couponCode;
    }

    public CouponCode checkCouponCode(String code) {

        CouponCode couponCode = couponCodeRepository.findByCouponCode(code).
                orElseThrow(() -> new NotFoundException(CouponCode.class, code));

        if(couponCode.isCodeValid()) {
            throw new CouponCodeExpiredException(code);
        }
        return couponCode;
    }
}
