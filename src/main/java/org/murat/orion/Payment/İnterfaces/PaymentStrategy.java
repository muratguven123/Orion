package org.murat.orion.Payment.İnterfaces;

import org.murat.orion.Payment.Dto.Request.PaymentRequest;
import org.murat.orion.Payment.Entity.PaymentTransactionType;
import org.springframework.stereotype.Service;

@Service
public interface PaymentStrategy {
    PaymentTransactionType getPaymentType();
    void processPayment(PaymentRequest request);
}
//Todo:"Önce isteği kontrol et. Geçerliyse deftere 'Başladı' yaz.
// Sonra gönderenin parasını azalt ve alıcının parasını artır.
// Eğer ikisi de sorunsuz olursa deftere 'Bitti' yaz.
// Eğer bir sorun çıkarsa, paraları eski haline getir ve deftere 'Hata' yaz."