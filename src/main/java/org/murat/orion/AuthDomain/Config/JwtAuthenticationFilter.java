package org.murat.orion.AuthDomain.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.murat.orion.AuthDomain.Entity.User;
import org.murat.orion.AuthDomain.Repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // LOG 1: Header geliyor mu?
        System.out.println("LOG 1: Filtre çalıştı. Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("LOG 2: Header yok veya Bearer değil. Anonim devam ediliyor.");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // LOG 3: Token doğrulama öncesi
            boolean isTokenValidInitial = jwtService.validateToken(jwt);
            System.out.println("LOG 3: jwtService.validateToken sonucu: " + isTokenValidInitial);

            if (!isTokenValidInitial) {
                System.out.println("LOG 4: Token validasyonu başarısız oldu! Anonim devam ediliyor.");
                filterChain.doFilter(request, response);
                return;
            }

            final String userEmail = jwtService.extractUsername(jwt);
            System.out.println("LOG 5: Email çıkarıldı: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(userEmail).orElse(null);

                if (user != null) {
                    System.out.println("LOG 6: User DB'den bulundu: " + user.getEmail());

                    if (jwtService.isTokenValid(jwt, user)) {
                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        );

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                authorities
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("LOG 7: BAŞARILI! SecurityContext set edildi. Yetkiler: " + authorities);
                    } else {
                        System.out.println("LOG 7-HATA: isTokenValid(jwt, user) false döndü.");
                    }
                } else {
                    System.out.println("LOG 6-HATA: User DB'de bulunamadı.");
                }
            }
        } catch (Exception e) {
            System.out.println("LOG EXCEPTION: " + e.getMessage());
            logger.error("JWT Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
