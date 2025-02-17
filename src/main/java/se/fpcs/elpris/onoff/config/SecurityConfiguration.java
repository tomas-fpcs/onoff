package se.fpcs.elpris.onoff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import se.fpcs.elpris.onoff.security.CustomAuthenticationEntryPoint;
import se.fpcs.elpris.onoff.security.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  public static final RequestMatcher[] PUBLIC_PATHS = {
      new AntPathRequestMatcher("/swagger-ui/**"),
      new AntPathRequestMatcher("/v3/api-docs/**"),
      new AntPathRequestMatcher("/onoff/auth/**")};

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UserDetailsConfiguration userDetailsConfiguration;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Bean
  public Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer() {
    return csrf -> csrf
        .csrfTokenRepository(
            CookieCsrfTokenRepository.withHttpOnlyFalse());
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      Customizer<CsrfConfigurer<HttpSecurity>> csrfCustomizer)
      throws Exception {

    return http
        .csrf(csrfCustomizer)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(customAuthenticationEntryPoint)
        )
        .authorizeHttpRequests(auth ->
            auth.requestMatchers(PUBLIC_PATHS)
                .permitAll()
                .anyRequest()
                .authenticated())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(getAuthenticationProvider())
        .addFilterBefore(
            jwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class)
        .build();

  }

  private AuthenticationProvider getAuthenticationProvider() {

    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsConfiguration.userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;

  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
