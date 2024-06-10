package se.fpcs.elpris.onoff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.fpcs.elpris.onoff.converter.BooleanToStringHttpMessageConverter;
import se.fpcs.elpris.onoff.converter.StringToOutputTypeConverter;
import se.fpcs.elpris.onoff.converter.StringToPriceSourceConverter;
import se.fpcs.elpris.onoff.converter.StringToPriceZoneConverter;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPriceZoneConverter());
        registry.addConverter(new StringToOutputTypeConverter());
        registry.addConverter(new StringToPriceSourceConverter());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new BooleanToStringHttpMessageConverter());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8080", "https://onoff-qbnhglfqca-lz.a.run.app") //TODO move to config
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
