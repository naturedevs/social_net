package com.orbvpn.api.quartz;

import com.orbvpn.api.domain.dto.ScheduleEmailRequest;
import com.orbvpn.api.domain.dto.ScheduleEmailResponse;
import com.orbvpn.api.quartz.job.EmailJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EmailJobScheduler {

    Integer WAITING_SECONDS = 2;

    @Autowired
    private Scheduler scheduler;

    public ScheduleEmailResponse scheduleEmail(ScheduleEmailRequest scheduleEmailRequest) {
        scheduleEmailRequest = checkScheduleEmailRequestParams(scheduleEmailRequest);
        try {
            ZoneId zoneId = ZoneId.of(scheduleEmailRequest.getTimeZone());
            ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(), zoneId);
            if (dateTime.isBefore(ZonedDateTime.now())) {
                ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(false,
                        "dateTime must be after current time");
                return scheduleEmailResponse;
            }

            JobDetail jobDetail = buildJobDetail(scheduleEmailRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Email Scheduled Successfully!");
            return scheduleEmailResponse;
        } catch (SchedulerException ex) {
            log.error("Error scheduling email", ex);

            ScheduleEmailResponse scheduleEmailResponse = new ScheduleEmailResponse(false,
                    "Error scheduling email. Please try later!");
            return scheduleEmailResponse;
        }
    }

    private ScheduleEmailRequest checkScheduleEmailRequestParams(ScheduleEmailRequest scheduleEmailRequest) {
        if(scheduleEmailRequest.getDateTime() == null){
            scheduleEmailRequest.setDateTime(LocalDateTime.now().plusSeconds(WAITING_SECONDS));
        }

        if (scheduleEmailRequest.getTimeZone() == null){
            scheduleEmailRequest.setTimeZone(ZoneId.systemDefault().getId());
        }
        return scheduleEmailRequest;
    }

    private JobDetail buildJobDetail(ScheduleEmailRequest scheduleEmailRequest) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-jobs")
                .withDescription("Send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-triggers")
                .withDescription("Send Email Trigger")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}