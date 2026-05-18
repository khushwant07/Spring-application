package com.ecommerce.auth.security;

import com.ecommerce.auth.domain.User;
import com.ecommerce.auth.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var claims = jwtService.parse(token);
                Long userId = Long.parseLong(claims.getSubject());
                String rolesCsv = claims.get("roles", String.class);
                List<SimpleGrantedAuthority> authorities =
                        Arrays.stream(rolesCsv == null ? new String[0] : rolesCsv.split(","))
                                .filter(s -> !s.isBlank())
                                .map(String::trim)
                                .map(SimpleGrantedAuthority::new)
                                .toList();
                User user =
                        userRepository
                                .findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                var auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, authorities);
                auth.setDetails(user);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
