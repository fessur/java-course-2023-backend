package edu.java.client.implementation;

import edu.java.client.TrackerBotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.client.exception.BadRequestException;
import edu.java.controller.dto.ApiErrorResponse;
import edu.java.service.domain.Link;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class TrackerBotClientImpl implements TrackerBotClient {
    private final WebClient webClient;

    public TrackerBotClientImpl(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public void sendUpdate(Link link, String description, List<Long> chatIds) {
        webClient
            .post()
            .uri("/updates")
            .bodyValue(new LinkUpdateRequest(link.getId(), link.getUrl(), description, chatIds))
            .retrieve()
            .onStatus(HttpStatus.BAD_REQUEST::isSameCodeAs, clientResponse ->
                clientResponse.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new BadRequestException(apiErrorResponse)))
            )
            .bodyToMono(Void.class)
            .block();
    }
}
