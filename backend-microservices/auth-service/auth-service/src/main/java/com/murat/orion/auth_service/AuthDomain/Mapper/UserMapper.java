package com.murat.orion.auth_service.AuthDomain.Mapper;

import com.murat.orion.auth_service.AuthDomain.Dto.Request.RegisterRequest;
import com.murat.orion.auth_service.AuthDomain.Dto.Response.RegisterResponse;
import com.murat.orion.auth_service.AuthDomain.Entity.Role;
import com.murat.orion.auth_service.AuthDomain.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encodedPassword)
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .isActive(true)
                .isEmailVerified(false)
                .build();
    }

    public RegisterResponse toRegisterResponse(User user) {
        return RegisterResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .status("SUCCESS")
                .message("Kayıt başarıyla tamamlandı")
                .build();
    }
}

