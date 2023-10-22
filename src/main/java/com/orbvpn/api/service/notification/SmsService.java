package com.orbvpn.api.service.notification;

import com.orbvpn.api.config.sms.TwilioConfig;
import com.orbvpn.api.domain.entity.SmsRequest;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {
    @Autowired
    private TwilioConfig twilioConfig;

    public void sendRequest(SmsRequest smsRequest) {
        try {
            log.debug("sending Sms... " + smsRequest);
            MessageCreator creator = Message.creator(
                    new PhoneNumber(smsRequest.getPhoneNumber()),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    smsRequest.getMessage()
            );
            creator.create();
            log.debug("Sms was sent: " + smsRequest);
        } catch (Exception e) {
            log.error("failed to send Sms: " + smsRequest, e);
        }
    }
}
