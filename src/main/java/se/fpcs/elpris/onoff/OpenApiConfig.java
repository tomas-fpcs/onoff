package se.fpcs.elpris.onoff;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${onoff.hostname:localhost}")
  private String hostname;

  @Value("${onoff.port:8080}")
  private Integer port;

  @Value("${onoff.scheme:http}")
  private String scheme;

  @Bean
  public OpenAPI customOpenAPI() {
    List<Server> servers = new ArrayList<>();
    String url = scheme + "://" + hostname +
        (scheme.equals("https") ? "" : ":" + port);
    servers.add(new Server().url(url));
    return new OpenAPI().servers(servers);
  }
}
