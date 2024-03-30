package edu.java.bot.client.implementation;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.ApiErrorResponse;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.BadRequestException;
import edu.java.bot.client.exception.ConflictException;
import edu.java.bot.client.exception.NotFoundException;
import edu.java.bot.client.exception.TooManyRequestsException;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClientImpl implements ScrapperClient {
    private final WebClient webClient;
    private final RetryTemplate retryTemplate;
    private static final String LINKS_API_URL = "/links";
    private static final String CHAT_API_URL = "/tg-chat";
    private static final String CHAT_HEADER = "Tg-Chat-Id";

    public ScrapperClientImpl(String baseUrl, RetryTemplate retryTemplate) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.retryTemplate = retryTemplate;
    }

    @Override
    public ListLinksResponse fetchLinks(long chatId) {
        return retryTemplate.execute(context -> webClient
            .get()
            .uri(LINKS_API_URL)
            .header(CHAT_HEADER, Long.toString(chatId))
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, NotFoundException::new)
            )
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, TooManyRequestsException::new)
            )
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
            .bodyToMono(ListLinksResponse.class)
            .block());
    }

    @Override
    public void registerChat(long chatId) {
        retryTemplate.execute(context -> webClient
            .post()
            .uri(CHAT_API_URL + "/" + chatId)
            .retrieve()
            .onStatus(HttpStatus.CONFLICT::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, ConflictException::new)
            )
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, TooManyRequestsException::new)
            )
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
            .bodyToMono(Void.class)
            .block());
    }

    @Override
    public LinkResponse trackLink(long chatId, String link) {
        return retryTemplate.execute(context -> webClient
            .post()
            .uri(LINKS_API_URL)
            .header(CHAT_HEADER, Long.toString(chatId))
            .bodyValue(new AddLinkRequest(link))
            .retrieve()
            .onStatus(HttpStatus.CONFLICT::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, ConflictException::new)
            )
            .onStatus(HttpStatus.NOT_FOUND::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, NotFoundException::new)
            )
            .onStatus(HttpStatus.BAD_REQUEST::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, BadRequestException::new)
            )
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, TooManyRequestsException::new)
            )
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
            .bodyToMono(LinkResponse.class)
            .block());
    }

    @Override
    public LinkResponse untrackLink(long chatId, String link) {
        return retryTemplate.execute(context -> webClient
            .method(HttpMethod.DELETE)
            .uri(LINKS_API_URL)
            .header(CHAT_HEADER, Long.toString(chatId))
            .bodyValue(new RemoveLinkRequest(link))
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, NotFoundException::new)
            )
            .onStatus(HttpStatus.BAD_REQUEST::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, BadRequestException::new)
            )
            .onStatus(HttpStatus.TOO_MANY_REQUESTS::isSameCodeAs, clientResponse ->
                handleStatusCode(clientResponse, TooManyRequestsException::new)
            )
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.createException().flatMap(Mono::error))
            .bodyToMono(LinkResponse.class)
            .block());
    }

    private Mono<Exception> handleStatusCode(
        ClientResponse clientResponse,
        Function<ApiErrorResponse, Exception> constructor
    ) {
        return clientResponse.bodyToMono(ApiErrorResponse.class)
            .flatMap(apiErrorResponse -> Mono.error(constructor.apply(apiErrorResponse)));
    }
}
