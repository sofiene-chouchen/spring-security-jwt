package com.jirademo.jiraapi.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


  private final JwtAuthenticationFilter jwtAuthFilter ;

  private final AuthenticationProvider authenticationProvider ;
  //* spring bech ylawej ala securityfilrechaine as a bean
  //! lena nzido el white list mta3na eli houma el route eli nst7a9ouch fiha token
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    //! list mta3 req patern app
                    .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    .anyRequest()
                    //! others req need to be authenticated
                    .authenticated())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //! the spring will create a session in each request
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter , UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
