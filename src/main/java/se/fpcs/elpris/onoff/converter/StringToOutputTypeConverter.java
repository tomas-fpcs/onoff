package se.fpcs.elpris.onoff.converter;

import org.springframework.core.convert.converter.Converter;
import se.fpcs.elpris.onoff.rest.OutputType;

public class StringToOutputTypeConverter implements Converter<String, OutputType> {

  @Override
  public OutputType convert(String source) {
    return OutputType.valueOf(source.toUpperCase());
  }
}
