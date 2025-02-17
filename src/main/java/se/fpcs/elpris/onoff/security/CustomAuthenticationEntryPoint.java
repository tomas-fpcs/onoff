package se.fpcs.elpris.onoff.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import se.fpcs.elpris.onoff.config.ErrorResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    final String json = objectMapper.writeValueAsString(
        ErrorResponse.builder()
            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .message(authException.getMessage())
            .status(HttpStatus.UNAUTHORIZED.value())
            .build());

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(json);
    response.getWriter().flush();
    response.getWriter().close();

  }

}
