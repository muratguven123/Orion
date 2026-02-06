package org.murat.orion.AuthDomain.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailLoginRequest {

    /**
     * Kullanıcı email adresi
     */
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    /**
     * Kullanıcı şifresi
     */
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
}

