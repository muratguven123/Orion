package org.murat.orion.AuthDomain.Dto.Response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpResponse {

    /**
     * İşlem durumu (OTP_SENT, ERROR)
     */
    private String status;

    /**
     * Kullanıcıya gösterilecek mesaj
     */
    private String message;

    /**
     * OTP gönderilen telefon numarası (maskelenmiş)
     */
    private String phoneNumber;

    /**
     * OTP'nin geçerlilik süresi (saniye)
     */
    private Integer expiresInSeconds;

    /**
     * İşlem zamanı
     */
    private LocalDateTime timestamp;
}
