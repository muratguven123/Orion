package com.murat.orion.auth_service.AuthDomain.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "otp_codes", schema = "identity")
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * OTP gönderilen telefon numarası
     */
    @Column(nullable = false)
    private String phoneNumber;

    /**
     * Oluşturulan OTP kodu (6 haneli)
     */
    @Column(nullable = false)
    private String code;

    /**
     * OTP oluşturulma zamanı
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * OTP son geçerlilik zamanı
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * OTP kullanıldı mı?
     */
    @Column(nullable = false)
    private Boolean isUsed = false;

    /**
     * Deneme sayısı (brute force koruması)
     */
    @Column(nullable = false)
    private Integer attempts = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * OTP'nin süresi dolmuş mu kontrol eder
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * OTP geçerli mi kontrol eder
     */
    public boolean isValid() {
        return !isUsed && !isExpired() && attempts < 3;
    }
}

