package com.trandieu.moneymanager.security;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.trandieu.moneymanager.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
   private final UserDetailsService userDetailsService;
   private final JwtUtil jwtUtil;

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
         throws ServletException, IOException {
      final String authHeader = request.getHeader("Authorization");
      String email = null;
      String jwt = null;

      if(authHeader != null && authHeader.startsWith("Bearer ")) {
         jwt = authHeader.substring(7);
         email = jwtUtil.extractEmail(jwt);
         
      }
   }
}
