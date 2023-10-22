package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.ResellerUserCreate;
import com.orbvpn.api.domain.dto.ResellerUserEdit;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.reseller.ResellerUserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

@Component
@RequiredArgsConstructor
@Validated
public class ResellerUserMutation implements GraphQLMutationResolver {

    private final ResellerUserService resellerUserService;

    @RolesAllowed({ADMIN, RESELLER})
    public UserView resellerCreateUser(ResellerUserCreate resellerUserCreate) {
        return resellerUserService.createUser(resellerUserCreate);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserView resellerDeleteUser(int id) {
        return resellerUserService.deleteUserById(id);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserView resellerDeleteUserByEmail(String email) {
        return resellerUserService.deleteUserByEmail(email);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserView resellerEditUser(int id, ResellerUserEdit resellerUserEdit) {
        return resellerUserService.editUserById(id, resellerUserEdit);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserView resellerEditUserByEmail(String email, ResellerUserEdit resellerUserEdit) {
        return resellerUserService.editUserByEmail(email, resellerUserEdit);
    }
}
