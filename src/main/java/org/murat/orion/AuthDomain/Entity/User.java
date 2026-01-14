package org.murat.orion.AuthDomain.Entity;

import com.google.protobuf.DescriptorProtos;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", schema = "identity", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_phone", columnList = "phone_number")
})
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true)
    private String email;

    // Şifre (Sadece Email/Password stratejisinde dolu olur, SMS ile girerse boş olabilir)
    @Column(name = "password_hash")
    private String passwordHash;

    // SMS Stratejisi için gerekli
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(DescriptorProtos.FeatureSet.EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    /
    @Column(name = "role", nullable = false)
    private String role;

    // Audit Alanları (Otomatik dolar)
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
