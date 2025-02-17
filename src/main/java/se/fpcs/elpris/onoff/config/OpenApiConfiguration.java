package se.fpcs.elpris.onoff.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

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

    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // Apply globally
        .components(new Components()
            .addSecuritySchemes("bearerAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT") // Defines it as a JWT token
            )
        )
        .servers(servers);
  }

}
