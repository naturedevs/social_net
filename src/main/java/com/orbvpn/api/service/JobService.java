package com.orbvpn.api.service;

import com.orbvpn.api.reposiitory.RadAcctRepository;
import com.orbvpn.api.service.reseller.ResellerLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private static final int DAILY_RATE = 24 * 60 * 60 * 1000;
  private static final int HOUR_RATE = 60 * 60 * 1000;
  private static final int TEN_MINUTES_RATE = 10 * 60 * 1000;
  private static final int FIVE_MINUTES_RATE = 5 * 60 * 1000;
  private static final String EVERY_1AM_LONDON_TIME = "0 0 1 * * ?";

  private final HelpCenterService helpCenterService;
  private final PromotionService promotionService;
  private final ResellerLevelService resellerLevelService;
  private final RenewUserSubscriptionService renewUserSubscriptionService;
  private final MoreLoginCountService moreLoginCountService;
  private final RadAcctRepository radAcctRepository;
  private final ConnectionService connectionService;

  @Scheduled(fixedRate = HOUR_RATE)
  public void removeOldTickets() {
    log.info("Starting job for removing old tickets");
    helpCenterService.removeOldTickets();
    log.info("Removing old tickets job is finished");
  }

  @Scheduled(fixedRate = HOUR_RATE)
  public void updateResellerLevels() {
    log.info("Starting job for updating reseller level");
    resellerLevelService.updateResellersLevel();
    log.info("Finished job for updating reseller level");
  }

  @Scheduled(fixedRate = HOUR_RATE)
  public void renewSubscriptions() {
    log.info("Started renewing user subscriptions");
    renewUserSubscriptionService.renewSubscriptions();
    log.info("Finished job renewing subscriptions");
  }

  @Scheduled(fixedRate = HOUR_RATE)
  public void removeExpiredMoreLoginCount() {
    log.info("Started removing expired moore login count");
    moreLoginCountService.removeExpiredMoreLoginCount();
    log.info("Finished removing expired more login count");
  }

  @Scheduled(fixedRate = FIVE_MINUTES_RATE)
  public void removeAllRadacctTemporarily() {
    //https://freeradius-users.freeradius.narkive.com/5ULrgWHb/user-freezing
    log.info("Started removing all radacct records");
    radAcctRepository.deleteAllInBatch();
    moreLoginCountService.removeExpiredMoreLoginCount();
    log.info("Finished removing all radacct records");
  }

  @Scheduled(fixedRate = TEN_MINUTES_RATE)
  public void disconnectDeactivatedUsers() {
    log.info("Started Disconnecting Deactivated Users");
    connectionService.disconnectDeactivatedUsers();
    log.info("Finished Disconnecting Deactivated Users");
  }

  @Scheduled(fixedRate = DAILY_RATE)
  public void promotions() {
    log.info("Starting job for promotions");
    promotionService.runTask();
    log.info("promotions job is finished");
  }
}
