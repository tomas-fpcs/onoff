package se.fpcs.elpris.onoff.date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ntp")
@Getter
@Setter
public class NTPProperties {
    private String hostname;
}
