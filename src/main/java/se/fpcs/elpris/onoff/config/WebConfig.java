package se.fpcs.elpris.onoff.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.fpcs.elpris.onoff.converter.StringToOutputTypeConverter;
import se.fpcs.elpris.onoff.converter.StringToPriceSourceConverter;
import se.fpcs.elpris.onoff.converter.StringToPriceZoneConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPriceZoneConverter());
        registry.addConverter(new StringToOutputTypeConverter());
        registry.addConverter(new StringToPriceSourceConverter());
    }
}
