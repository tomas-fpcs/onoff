package se.fpcs.elpris.onoff.config;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final CorsProperties corsProperties;

    @Autowired
    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

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
                        .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                        .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                        .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                        .allowCredentials(corsProperties.isAllowCredentials());
            }
        };
    }
}
