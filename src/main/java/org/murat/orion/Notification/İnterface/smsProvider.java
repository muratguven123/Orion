package org.murat.orion.Notification.İnterface;

public interface smsProvider {
    void sendSms(String phoneNumber, String message);
    boolean supports(String provider);
}
