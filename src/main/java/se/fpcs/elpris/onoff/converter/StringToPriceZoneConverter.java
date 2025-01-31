package se.fpcs.elpris.onoff.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import se.fpcs.elpris.onoff.price.PriceZone;

@Component
public class StringToPriceZoneConverter implements Converter<String, PriceZone> {

  @Override
  public PriceZone convert(String source) {
    return PriceZone.valueOf(source.toUpperCase());
  }
}
