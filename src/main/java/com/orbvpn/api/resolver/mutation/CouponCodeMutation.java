package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.CouponCodeDto;
import com.orbvpn.api.domain.entity.CouponCode;
import com.orbvpn.api.service.CouponCodeService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
public class CouponCodeMutation implements GraphQLMutationResolver {

    private final CouponCodeService couponCodeService;

    @RolesAllowed(ADMIN)
    public CouponCode createCouponCode(CouponCodeDto couponCodeDto) {
        return couponCodeService.createCouponCode(couponCodeDto);
    }

    @RolesAllowed(ADMIN)
    public CouponCode updateCouponCode(CouponCodeDto couponCodeDto) {
        return couponCodeService.updateCouponCode(couponCodeDto);
    }

    public CouponCode checkCouponCode(String code) {
        return couponCodeService.checkCouponCode(code);
    }

    public CouponCode useCouponCode(String code) {
        return couponCodeService.useCouponCode(code);
    }
}
