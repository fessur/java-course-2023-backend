package edu.java.client.implementation;

import edu.java.client.TrackerBotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.BadRequestException;
import edu.java.controller.dto.ApiErrorResponse;
import edu.java.service.model.Link;
import java.util.Collection;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TrackerBotClientImpl implements TrackerBotClient {
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public TrackerBotClientImpl(String baseUrl, RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public void sendUpdate(Link link, String description, Collection<Long> chatIds) {
        retryTemplate.execute(context -> webClient
            .post()
            .uri("/updates")
            .bodyValue(new LinkUpdateRequest(link.getId(), link.getUrl(), description, chatIds))
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::isSameCodeAs, clientResponse ->
                clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new BadRequestException(apiErrorResponse)))
            )
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
            .bodyToMono(Void.class)
            .block());
    }
}
