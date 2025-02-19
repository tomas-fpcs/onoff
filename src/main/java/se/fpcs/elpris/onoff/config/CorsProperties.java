package se.fpcs.elpris.onoff.config;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public class CorsProperties {

  private final OpenAPI openAPI;

  private final List<String> allowedMethods =
      List.of("GET",
          "POST",
          "PUT",
          "DELETE",
          "OPTIONS");
  private  final List<String> allowedHeaders = List.of("*");
  private  final  boolean allowCredentials = true;

  public List<String> getAllowedOrigins() {

    return openAPI.getServers().stream()
        .map(server -> server.getUrl())
        .toList();

  }

}
