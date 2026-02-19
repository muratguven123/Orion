package com.murat.orion.auth_service.AuthDomain.Repository;

import com.murat.orion.auth_service.AuthDomain.Entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    /**
     * Telefon numarasına göre en son oluşturulan ve kullanılmamış OTP'yi bulur
     */
    Optional<OtpCode> findTopByPhoneNumberAndIsUsedFalseOrderByCreatedAtDesc(String phoneNumber);

    /**
     * Telefon numarasına göre tüm kullanılmamış OTP'leri siler
     */
    void deleteAllByPhoneNumberAndIsUsedFalse(String phoneNumber);
}

