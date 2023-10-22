package com.orbvpn.api.service.reseller;

import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.InternalException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.*;
import com.orbvpn.api.reposiitory.*;
import com.orbvpn.api.service.PasswordService;
import com.orbvpn.api.service.RoleService;
import com.orbvpn.api.service.ServiceGroupService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ResellerService {

    @Setter
    private ServiceGroupService serviceGroupService;
    private final PasswordService passwordService;
    private final RoleService roleService;

    private final ResellerViewMapper resellerViewMapper;
    private final ResellerEditMapper resellerEditMapper;
    private final ResellerLevelEditMapper resellerLevelEditMapper;
    private final ResellerLevelViewMapper resellerLevelViewMapper;
    private final ResellerCreditViewMapper resellerCreditViewMapper;
    private final ResellerLevelCoefficientsMapper resellerLevelCoefficientsMapper;

    private final UserRepository userRepository;
    private final ResellerRepository resellerRepository;
    private final ResellerAddCreditRepository resellerAddCreditRepository;
    private final ResellerLevelRepository resellerLevelRepository;
    private final ResellerLevelCoefficientsRepository resellerLevelCoefficientsRepository;

    public Reseller getOwnerReseller() {
        return getResellerById(1);
    }

    public ResellerView createReseller(ResellerCreate resellerCreate) {
        log.info("Creating a reseller with data {} ", resellerCreate);

        Reseller reseller = resellerEditMapper.create(resellerCreate);
        reseller.setLevelSetDate(LocalDateTime.now());

        User user = reseller.getUser();
        user.setRole(roleService.getByName(RoleName.RESELLER));
        passwordService.setPassword(user, resellerCreate.getPassword());

        resellerRepository.save(reseller);
        ResellerView resellerView = resellerViewMapper.toView(reseller);

        log.info("Created a reseller with view {}", resellerView);
        return resellerView;
    }

    public ResellerView getReseller(int id) {
        Reseller reseller = getResellerById(id);
        return resellerViewMapper.toView(reseller);
    }

    public List<ResellerView> getEnabledResellers() {
        return resellerRepository.findAllByEnabled(true)
                .stream()
                .map(resellerViewMapper::toView)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalResellersCredit() {
        BigDecimal total = BigDecimal.valueOf(0);
        List<Reseller> all = resellerRepository.findAll();
        for (Reseller reseller : all) {
            total = total.add(reseller.getCredit());
        }

        return total;
    }

    public ResellerView editReseller(int id, ResellerEdit resellerEdit) {
        log.info("Editing reseller with id {} with data {}", id, resellerEdit);

        Reseller reseller = getResellerById(id);
        resellerEditMapper.edit(reseller, resellerEdit);


        resellerRepository.save(reseller);
        ResellerView resellerView = resellerViewMapper.toView(reseller);

        log.info("Edited reseller {}", resellerView);
        return resellerView;
    }

    public ResellerView deleteReseller(int id) {
        log.info("Deleting reseller with id {}", id);
        Reseller reseller = getResellerById(id);

        resellerRepository.delete(reseller);

        return resellerViewMapper.toView(reseller);
    }

    public void deleteAllByUser(User user) {
//        userRepository.updateResellerId(user.getReseller().getId(), newResellerId);
        resellerRepository.deleteAllByUser(user);
    }

    public ResellerView addResellerServiceGroup(int resellerId, int serviceGroupId) {
        Reseller reseller = getResellerById(resellerId);
        ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

        reseller.getServiceGroups().add(serviceGroup);

        resellerRepository.save(reseller);
        return resellerViewMapper.toView(reseller);
    }

    public ResellerView removeResellerServiceGroup(int resellerId, int serviceGroupId) {
        Reseller reseller = getResellerById(resellerId);
        ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);

        reseller.getServiceGroups().remove(serviceGroup);

        resellerRepository.save(reseller);
        return resellerViewMapper.toView(reseller);
    }

    public ResellerView setResellerLevel(int resellerId, ResellerLevelName name) {
        log.info("Updating reseller {} level {}", resellerId, name);
        Reseller reseller = getResellerById(resellerId);

        reseller.setLevel(getResellerLevel(name));
        reseller.setLevelSetDate(LocalDateTime.now());
        resellerRepository.save(reseller);

        log.info("Reseller {} updated level {}", resellerId, name);

        return resellerViewMapper.toView(reseller);
    }

    public ResellerView addResellerCredit(int resellerId, BigDecimal credit) {
        log.info("Adding reseller {} credit {}", resellerId, credit);
        Reseller reseller = getResellerById(resellerId);

        BigDecimal curCredit = reseller.getCredit();
        reseller.setCredit(curCredit.add(credit));
        ResellerAddCredit resellerAddCredit = new ResellerAddCredit(reseller, credit);
        resellerRepository.save(reseller);
        resellerAddCreditRepository.save(resellerAddCredit);

        log.info("Added reseller {} credit {}", resellerId, credit);

        return resellerViewMapper.toView(reseller);
    }

    public ResellerView deductResellerCredit(int resellerId, BigDecimal credit) {
        log.info("Deducting reseller {} credit {}", resellerId, credit);
        Reseller reseller = getResellerById(resellerId);

        BigDecimal curCredit = reseller.getCredit();
        reseller.setCredit(curCredit.subtract(credit));
        ResellerAddCredit resellerAddCredit = new ResellerAddCredit(reseller, credit.negate());
        resellerRepository.save(reseller);
        resellerAddCreditRepository.save(resellerAddCredit);

        log.info("Deduct reseller {} credit {}", resellerId, credit);

        return resellerViewMapper.toView(reseller);
    }


    //Only should be called when service group is removed
    public void removeServiceGroup(ServiceGroup serviceGroup) {
        List<Reseller> resellers = resellerRepository.findAll();
        resellers.forEach(reseller -> {
            reseller.getServiceGroups().remove(serviceGroup);
        });

        resellerRepository.saveAll(resellers);
    }

    public Reseller getResellerById(int id) {
        return resellerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Reseller.class, id));
    }

    public Reseller getResellerByUser(User user) {
        return resellerRepository.findResellerByUser(user)
                .orElseThrow(() -> new InternalException("Can't find reseller"));
    }

    public ResellerLevel getResellerLevel(ResellerLevelName resellerLevelName) {
        return resellerLevelRepository.getByName(resellerLevelName);
    }

    public ResellerLevelView updateResellerLevel(int id, ResellerLevelEdit levelEdit) {
        log.info("Updating reseller level {} to {}", id, levelEdit);

        ResellerLevel level = getResellerLevel(id);
        resellerLevelEditMapper.edit(level, levelEdit);
        resellerLevelRepository.save(level);

        return resellerLevelViewMapper.toView(level);
    }

    public ResellerLevelCoefficientsView updateResellerLevelCoefficients(
            ResellerLevelCoefficientsEdit resellerLevelCoefficientsEdit) {
        log.info("Updating reseller level coefficients {}", resellerLevelCoefficientsEdit);

        ResellerLevelCoefficients resellerLevelCoefficients = getResellerLevelCoefficientsEntity();
        resellerLevelCoefficientsRepository.save(resellerLevelCoefficients);
        return resellerLevelCoefficientsMapper.toView(resellerLevelCoefficients);
    }

    public List<ResellerLevelView> getResellersLevels() {
        return resellerLevelRepository.findAll()
                .stream()
                .map(resellerLevelViewMapper::toView)
                .collect(Collectors.toList());
    }

    public List<ResellerCreditView> getResellersCredits() {
        return resellerRepository.findAll()
                .stream()
                .map(resellerCreditViewMapper::toView)
                .collect(Collectors.toList());
    }

    public ResellerLevelCoefficientsView getResellerLevelCoefficients() {
        ResellerLevelCoefficients resellerLevelCoefficients = getResellerLevelCoefficientsEntity();
        return resellerLevelCoefficientsMapper.toView(resellerLevelCoefficients);
    }

    public ResellerLevel getResellerLevel(int id) {
        return resellerLevelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ResellerLevel.class, id));
    }

    public ResellerLevelCoefficients getResellerLevelCoefficientsEntity() {
        return resellerLevelCoefficientsRepository.findById(1)
                .orElseThrow(() -> new NotFoundException(ResellerLevel.class, 1));
    }
}
