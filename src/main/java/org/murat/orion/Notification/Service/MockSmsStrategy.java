package org.murat.orion.Notification.Service;

import lombok.extern.slf4j.Slf4j;
import org.murat.orion.Notification.İnterface.smsProvider;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MockSmsStrategy implements smsProvider {
    @Override
    public void sendSms(String phoneNumber, String message) {

    }
}
