package com.jirademo.jiraapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService ;
  private final  UserDetailsService UserDetailsService;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  )
          throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization"); //? get the token from the headers
    final String jwt ;
    final String userEmail ;

    if (authHeader == null || authHeader.startsWith("Bearer ")){ //? test if the token is on header and have a Bearer
      filterChain.doFilter(request , response);
      return; //* if not we dont need to complete
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUsername(jwt); //? bech tjebed username me jwt
    //? if user not connect yet
    if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.UserDetailsService.loadUserByUsername(userEmail);
      if (jwtService.isTokenValidate(jwt , userDetails)){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request , response); //? pass to the next filter
  }
}
