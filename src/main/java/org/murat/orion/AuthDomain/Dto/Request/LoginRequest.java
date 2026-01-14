package org.murat.orion.AuthDomain.Dto.Request;

import jakarta.validation.constraints.Email;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    private String password;

    private String phoneNumber;

    private String verificationCode;
}
