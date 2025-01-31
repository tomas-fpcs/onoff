package se.fpcs.elpris.onoff.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_Client;

@Configuration
@Log4j2
public class OnOffConfiguration {

  @Bean
  public WebClient webClient() {

    try {
      //TODO should use secure client
      SslContext context = SslContextBuilder.forClient()
          .trustManager(InsecureTrustManagerFactory.INSTANCE)
          .build();

      HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

      return WebClient
          .builder()
          .clientConnector(new ReactorClientHttpConnector(httpClient))
          .baseUrl("https://www.elprisetjustnu.se")
          .build();
    } catch (Exception e) {
      log.error("Could not create webClient() {}: {}", e.getClass().getName(), e.getMessage(), e);
      throw new CouldNotCreateWebClientException();
    }

  }


  @Bean
  public EPJN_Client elPrisetJustNuClient(WebClient webClient) {
    return HttpServiceProxyFactory
        .builderFor(WebClientAdapter.create(webClient))
        .build()
        .createClient(EPJN_Client.class);
  }
}
