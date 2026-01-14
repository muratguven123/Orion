package org.murat.orion.AuthDomain.Service;

import lombok.RequiredArgsConstructor;
import org.murat.orion.AuthDomain.Dto.Request.RegisterRequest;
import org.murat.orion.AuthDomain.Dto.Response.RegisterResponse;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Mapper.UserMapper;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email adresi zaten kayıtlı");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userMapper.toEntity(request, encodedPassword);

        User savedUser = userRepository.save(user);

        return userMapper.toRegisterResponse(savedUser);
    }
}
