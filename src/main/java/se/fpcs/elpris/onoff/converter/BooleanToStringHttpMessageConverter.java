package se.fpcs.elpris.onoff.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;

public class BooleanToStringHttpMessageConverter extends AbstractHttpMessageConverter<Boolean> {

    public BooleanToStringHttpMessageConverter() {
        super(new MediaType("text", "plain"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Boolean.class.equals(clazz);
    }

    @Override
    protected Boolean readInternal(Class<? extends Boolean> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeInternal(Boolean bool, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(bool.toString().toLowerCase().getBytes());
    }
}
