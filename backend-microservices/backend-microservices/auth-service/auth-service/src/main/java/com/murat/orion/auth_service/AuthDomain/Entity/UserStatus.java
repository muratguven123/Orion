package com.murat.orion.auth_service.AuthDomain.Entity;

/**
 * Kullanıcı hesap durumları
 */
public enum UserStatus {
    /**
     * Aktif kullanıcı - Sisteme giriş yapabilir
     */
    ACTIVE,

    /**
     * Pasif kullanıcı - Geçici olarak devre dışı
     */
    INACTIVE,

    /**
     * Email/SMS doğrulaması bekliyor
     */
    PENDING_VERIFICATION,

    /**
     * Hesap kilitli - Güvenlik nedeniyle
     */
    LOCKED,

    /**
     * Hesap silinmiş (soft delete)
     */
    DELETED
}

