package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.entity.SmsRequest;
import com.orbvpn.api.service.notification.SmsService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
public class SmsMutation implements GraphQLMutationResolver {
    private final SmsService smsService;

    @RolesAllowed(ADMIN)
    public Boolean sendSms(SmsRequest smsRequest) {
        smsService.sendRequest(smsRequest);
        return true;
    }
}
