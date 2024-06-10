package se.fpcs.elpris.onoff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("test")
public class MockAuthFilter implements jakarta.servlet.Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        httpServletRequest.setAttribute(User.class.getSimpleName(), new User("TestUser"));
        filterChain.doFilter(servletRequest, servletResponse);


    }

}

