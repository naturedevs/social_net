package com.orbvpn.api.service.reseller;

import com.orbvpn.api.domain.dto.ResellerScoreDto;
import com.orbvpn.api.domain.dto.ResellerScoreLimitEdit;
import com.orbvpn.api.domain.entity.ResellerScoreLimit;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.ResellerScoreLimitRepository;
import com.orbvpn.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResellerScoreService {

    private final ResellerScoreLimitRepository resellerScoreLimitRepository;
    private final ResellerSaleService resellerSaleService;
    private final UserService userService;

    public List<ResellerScoreLimit> getScoreLimits() {
        return resellerScoreLimitRepository.findAll();
    }

    public ResellerScoreLimit updateScoreBySymbol(ResellerScoreLimitEdit limitEdit) {

        ResellerScoreLimit scoreToUpdate = resellerScoreLimitRepository.findOneBySymbol(limitEdit.getSymbol()).
                orElse(null);

        if(scoreToUpdate == null) {
            throw new NotFoundException(String.format("Reseller score with the symbol %s could not found.", limitEdit.getSymbol()));
        }

        scoreToUpdate.setMaximumLimit(limitEdit.getMaxLimit());
        resellerScoreLimitRepository.save(scoreToUpdate);
        return  scoreToUpdate;
    }

    public List<ResellerScoreLimit> updateResellerScoreLimits(List<ResellerScoreLimitEdit> resellerScoreLimitEdits) {

        List<ResellerScoreLimit> updatedResellerScores = new ArrayList<>();

        for(ResellerScoreLimitEdit limitEdit : resellerScoreLimitEdits) {
            updatedResellerScores.add(updateScoreBySymbol(limitEdit));
        }
        return updatedResellerScores;
    }

    public ResellerScoreDto calculateResellerScore(int resellerId) {
        Double resellerScore = 0.0;
        List<ResellerScoreLimit> manualPercentages = getScoreLimits();

        for(ResellerScoreLimit scoreLimit: manualPercentages) {
            resellerScore += getScore(scoreLimit, resellerId);
        }

        return new ResellerScoreDto(resellerScore, resellerId);
    }

    public Double getScore(ResellerScoreLimit resellerScoreLimit, int resellerId) {
        double score;
        switch (resellerScoreLimit.getSymbol()){
            case "Cm":
            case "B":
                double lastMonthSales = resellerSaleService.getLastMonthSalesOfReseller(resellerId).size();
                score = lastMonthSales * resellerScoreLimit.getMaximumLimit();
                break;
            case "Ua":
                int activeUsersOfReseller = userService.getActiveUserCountOfReseller(resellerId);
                score = activeUsersOfReseller * resellerScoreLimit.getMaximumLimit();
                break;
            case "L":
                double orbVpnLifetime = ChronoUnit.DAYS.between(LocalDate.of(2021,11,30), LocalDate.now());
                score = orbVpnLifetime * resellerScoreLimit.getMaximumLimit();
                break;
            case "Di":
                score = resellerScoreLimit.getMaximumLimit();
                break;
            case "St":
                double totalSales = resellerSaleService.getTotalSaleOfReseller(resellerId).size();
                score = totalSales * resellerScoreLimit.getMaximumLimit();
                break;
            case "Sm":
                double monthlySales = resellerSaleService.getMonthlySalesOfReseller(resellerId);
                score = monthlySales * resellerScoreLimit.getMaximumLimit();
                break;
            default:
                score = 0.0;
                break;
        }

        return score / 100;
    }


}

