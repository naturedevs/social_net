package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT_NATIVE;

import java.io.Console;
import java.util.List;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.reposiitory.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserViewMapper userViewMapper;

    private final UserSubscriptionService userSubscriptionService;

    public int getTotalActiveUsers() {
        return userRepository.getTotalActiveUsers();
    }

    public Page<UserView> getActiveUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

        return userRepository.findAllActiveUsers(pageable)
                .map(userViewMapper::toView);
    }

    public Page<UserView> getInactiveUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT_NATIVE));

        return userRepository.findAllNotActiveUsers(pageable)
                .map(userViewMapper::toView);
    }

    public Page<UserView> getAllUsers(boolean sort, int page, int size, String param, String query) {
        Direction dir = sort? Direction.ASC: Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "id"));

        Page<User> results;

        if (param.equals("email")) {
            
            results = userRepository.findByRoleIdAndEmailContaining(3, query, pageable);
                        // .map(userViewMapper::toView);
        } else { 
            results = userRepository.findByRoleIdAndUsernameContaining(3, query, pageable);
                        // .map(userViewMapper::toView);
        }

        results.forEach(user -> {
            user.setSubscription(userSubscriptionService.getCurrentSubscription(user));
        });

        return results.map(userViewMapper::toView);


        // return userRepository.findByRoleIdAndEmailContaining(3, query, pageable)
        //             .map(userViewMapper::toView);
        // if (param == null) {

            
        // }

        // else if (param.equals("email"))
        //     return userRepository.findAllUsers(query, pageable)
        //             .map(userViewMapper::toView);
        // else
        //     return userRepository.findAll(pageable)
        //             .map(userViewMapper::toView);
    }

}
