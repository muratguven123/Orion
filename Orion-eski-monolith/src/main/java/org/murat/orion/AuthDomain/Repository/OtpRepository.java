package org.murat.orion.AuthDomain.Repository;

import org.murat.orion.AuthDomain.Entity.OtpCode;
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

