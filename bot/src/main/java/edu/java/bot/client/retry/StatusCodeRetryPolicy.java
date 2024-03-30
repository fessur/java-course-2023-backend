package edu.java.bot.client.retry;

import java.util.Set;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class StatusCodeRetryPolicy extends SimpleRetryPolicy {
    private final Set<HttpStatusCode> statusCodes;

    public StatusCodeRetryPolicy(Set<HttpStatusCode> statusCodes) {
        this.statusCodes = statusCodes;
    }

    public StatusCodeRetryPolicy(int maxAttempts, Set<HttpStatusCode> statusCodes) {
        super(maxAttempts);
        this.statusCodes = statusCodes;
    }

    @Override
    public boolean canRetry(RetryContext context) {
        Throwable lastThrowable = context.getLastThrowable();
        if (lastThrowable instanceof HttpClientErrorException clientError) {
            return statusCodes.contains(clientError.getStatusCode());
        }
        if (lastThrowable instanceof HttpServerErrorException serverError) {
            return statusCodes.contains(serverError.getStatusCode());
        }
        return super.canRetry(context);
    }
}
