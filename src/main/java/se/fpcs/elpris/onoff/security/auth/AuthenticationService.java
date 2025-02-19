package se.fpcs.elpris.onoff.security.auth;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.security.JwtService;
import se.fpcs.elpris.onoff.security.Role;
import se.fpcs.elpris.onoff.security.UserRepository;
import se.fpcs.elpris.onoff.user.User;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest registerRequest) {

    if (log.isTraceEnabled()) {
      log.trace("register, email: {}", registerRequest.getEmail());
    }

    var user = User.builder()
        .firstname(registerRequest.getFirstname())
        .lastname(registerRequest.getLastname())
        .email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .roles(toRoleEnumSet(registerRequest.getRoles()))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .enabled(true)
        .build();

    User createdUser = userRepository.save(user);
    if (log.isTraceEnabled()) {
      log.trace(
          "register, jwt token created for: {}",
          createdUser == null ? "createdUser is NULL" : createdUser.getEmail());
    }

    var jwtToken = jwtService.generateToken(createdUser);

    if (log.isTraceEnabled()) {
      log.trace("register, jwt token created for: {}", registerRequest.getEmail());
    }

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();

  }

  private Set<Role> toRoleEnumSet(Set<String> roles) {

    return roles.stream()
        .map(role -> Role.valueOf(role)) //TODO handle bad enum value
        .collect(Collectors.toSet());

  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    if (log.isTraceEnabled()) {
      log.trace("authenticate, request.getEmail(): {}", request.getEmail());
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        ));

    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(); //TODO better error handling?

    if (log.isTraceEnabled()) {
      log.trace("authenticate, user.getEmail(): {}", user.getEmail());
    }

    var jwtToken = jwtService.generateToken(user);

    if (log.isTraceEnabled()) {
      log.trace("authenticate, jwt token created for: {}", request.getEmail());
    }

    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();

  }

}
