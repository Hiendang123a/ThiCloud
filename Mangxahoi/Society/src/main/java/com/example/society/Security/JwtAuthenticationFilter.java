package com.example.society.Security;

import com.example.society.Exception.AppException;
import com.example.society.Exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Bỏ qua kiểm tra token cho các API mở
        if (requestURI.startsWith("/api/account/") || requestURI.startsWith("/api/token/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (requestURI.startsWith("/ws") || requestURI.startsWith("/sockjs")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }


        String token = authHeader.substring(7);
        if(token.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (tokenProvider.validateToken(token)) {
                String userID = tokenProvider.getUserIDFromToken(token);
                UserDetails userDetails = User
                        .withUsername(userID)
                        .password("")
                        .roles("USER")
                        .build();

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
        filterChain.doFilter(request, response);
    }
}