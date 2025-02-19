package se.fpcs.elpris.onoff.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class OpenApiConfiguration {

  @Bean
  public OpenAPI customOpenAPI() {

    final String url;
    final String serviceName = System.getenv("K_SERVICE");
    log.trace("serviceName: {}", serviceName);
    final String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
    log.trace("projectId: {}", projectId);
    final String region = System.getenv("DEPLOY_REGION");
    log.trace("region: {}", region);

    if (serviceName != null && projectId != null && region != null) {
      url = "https://" + serviceName + "-" + projectId + "." + region + ".run.app";
    } else {
      url = "http://localhost:8080";
    }
    log.info("OpenApi base url: {}", url);

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
        .servers(List.of(new Server().url(url)));
  }

}
