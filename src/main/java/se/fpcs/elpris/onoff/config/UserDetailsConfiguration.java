package se.fpcs.elpris.onoff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import se.fpcs.elpris.onoff.security.UserRepository;

@Configuration
@RequiredArgsConstructor
public class UserDetailsConfiguration {

  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }
}
