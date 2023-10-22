package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.PromotionType;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.reposiitory.PromotionTypeRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final UserRepository userRepository;
    private final PromotionTypeRepository promotionTypeRepository;

    public void runTask(){

    }

    public void BirthdayPromotion(){
        PromotionType birthdayPromotion = promotionTypeRepository.findPromotionTypeByName("birthday");
        PromotionType yearsPromotion = promotionTypeRepository.findPromotionTypeByName("years");
        int numberOfYears = yearsPromotion.getValue();
        int numberOfinvitations = yearsPromotion.getValue();
        List<User> users = userRepository.findAll();
        for (User user: users) {
            if (user.getProfile().getBirthDate() == LocalDate.now()){
            }
            if (user.getCreatedAt().isBefore(LocalDateTime.now().minusYears(numberOfYears))){
            }
            if (user.getReferralCode() != null && user.getReferralCode().getInvitations() > numberOfinvitations){
            }
        }
    }
}
