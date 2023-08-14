package com.jirademo.jiraapi.auth;


import com.jirademo.jiraapi.config.JwtService;
import com.jirademo.jiraapi.user.Role;
import com.jirademo.jiraapi.user.User;
import com.jirademo.jiraapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ADMIN)
            .build();
    repository.save(user);
    var jwt = jwtService.genrateToken(user);
    return AuthenticationResponse.builder()
            .token(jwt)
            .build();
  }

  public AuthenticationResponse login(LoginRequest request) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    //? if it coneect we need to genrate a token
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
    var jwt = jwtService.genrateToken(user);
    return AuthenticationResponse.builder()
            .token(jwt)
            .build();
  }
}
