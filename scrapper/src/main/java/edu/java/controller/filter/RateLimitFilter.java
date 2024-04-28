package edu.java.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.controller.buckets.Limiter;
import edu.java.controller.dto.ApiErrorResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class RateLimitFilter implements Filter {
    private final Limiter limiter;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(Limiter limiter, ObjectMapper objectMapper) {
        this.limiter = limiter;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (!limiter.tryConsume(httpServletRequest.getRemoteAddr())) {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(new ApiErrorResponse(
                "Too many requests.",
                Integer.toString(HttpStatus.TOO_MANY_REQUESTS.value())
            )));
            return;
        }
        chain.doFilter(request, response);
    }
}
