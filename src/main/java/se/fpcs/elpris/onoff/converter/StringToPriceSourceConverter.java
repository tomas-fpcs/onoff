package se.fpcs.elpris.onoff.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import se.fpcs.elpris.onoff.price.PriceSource;

@Component
public class StringToPriceSourceConverter implements Converter<String, PriceSource> {

  @Override
  public PriceSource convert(String source) {
    return PriceSource.valueOf(source.toUpperCase());
  }
}
