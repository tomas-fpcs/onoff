package se.fpcs.elpris.onoff.security;

import static se.fpcs.elpris.onoff.config.SecurityConfiguration.PUBLIC_PATHS;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final RequestMatcher[] PUBLIC_PATHS_MATCHERS =
      Arrays.stream(PUBLIC_PATHS)
          .map(AntPathRequestMatcher::new)
          .toArray(RequestMatcher[]::new);

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return Arrays.stream(PUBLIC_PATHS_MATCHERS)
        .anyMatch(matcher -> matcher.matches(request));
  }

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeaderValue = request.getHeader("Authorization");
    if (authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
      if (log.isTraceEnabled()) {
        log.trace("No JWT specified in Authorization header");
      }
      throw new AuthenticationCredentialsNotFoundException(
          "Missing or invalid Authorization header");
    }

    final String jwt = authHeaderValue.substring(7);
    final Optional<String> userEmail = jwtService.extractUsername(jwt);

    if (userEmail.isEmpty()) {
      if (log.isTraceEnabled()) {
        log.trace("Could not extract subject (username/email) from JWT");
      }
      throw new BadCredentialsException("Invalid JWT");
    }

    final boolean isNotAuthenticated =
        SecurityContextHolder.getContext().getAuthentication() == null;

    if (isNotAuthenticated) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail.get());
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        if (log.isTraceEnabled()) {
          log.trace("JWT token is not valid");
        }
        throw new BadCredentialsException("Invalid JWT");
      }
      filterChain.doFilter(request, response);
    }

  }

}
