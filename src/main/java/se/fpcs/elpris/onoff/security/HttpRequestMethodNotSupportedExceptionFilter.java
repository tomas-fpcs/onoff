package se.fpcs.elpris.onoff.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.OncePerRequestFilter;
import se.fpcs.elpris.onoff.config.ErrorResponse;

@Component
@NoArgsConstructor
@Log4j2
public class HttpRequestMethodNotSupportedExceptionFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain)
      throws ServletException, IOException {

    try {
      chain.doFilter(request, response);
    } catch (HttpRequestMethodNotSupportedException e) {

      final String json = new ObjectMapper().writeValueAsString(
          ErrorResponse.builder()
              .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
              .message(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
              .status(HttpStatus.METHOD_NOT_ALLOWED.value())
              .build());

      response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
      response.setContentType("application/json");
      response.getWriter().write(json);
      response.getWriter().flush();
      response.getWriter().close();
    }

  }

}