package se.fpcs.elpris.onoff.security.auth;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import se.fpcs.elpris.onoff.security.Role;
import se.fpcs.elpris.onoff.security.UserRepository;

@Component
@RequiredArgsConstructor
@Log4j2
public class AdminInitializer implements CommandLineRunner {


  private final String ONOFF_ADMIN_PASSWORD_KEY = "ONOFF_ADMIN_PASSWORD";
  private final String ONOFF_ADMIN_PASSWORD = System.getenv(ONOFF_ADMIN_PASSWORD_KEY);

  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {

    requireNonNull(ONOFF_ADMIN_PASSWORD,
        "Environment variable " + ONOFF_ADMIN_PASSWORD_KEY + " not set");

    if (userRepository.count() == 0) { // ✅ Only create an admin if DB is empty

      final String email = "tomas@fpcs.se"; //TODO make configurable

      authenticationService.register(
          RegisterRequest.builder()
              .firstname("Admin")
              .lastname("User")
              .email(email)
              .password(ONOFF_ADMIN_PASSWORD)
              .roles(Set.of(Role.ROLE_ADMIN.name()))
              .build());

      log.info("Admin user created: {}", email);
    }

  }
}
