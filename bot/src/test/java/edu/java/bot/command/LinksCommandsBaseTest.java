package edu.java.bot.command;

import edu.java.bot.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.dto.ApiErrorResponse;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.exception.BadRequestException;
import edu.java.bot.client.exception.ConflictException;
import edu.java.bot.client.exception.NotFoundException;
import edu.java.bot.util.CommonUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LinksCommandsBaseTest {
    @Mock
    protected ScrapperClient scrapperClient;
    protected static final List<String> LINKS = List.of(
        "https://stackoverflow.com/questions/927358/how-do-i-undo-the-most-recent-local-commits-in-git",
        "https://stackoverflow.com/questions/2003505/how-do-i-delete-a-git-branch-locally-and-remotely",
        "https://stackoverflow.com/questions/292357/what-is-the-difference-between-git-pull-and-git-fetch",
        "https://stackoverflow.com/questions/477816/which-json-content-type-do-i-use",
        "https://stackoverflow.com/questions/348170/how-do-i-undo-git-add-before-commit",
        "https://github.com/spring-projects/spring-framework",
        "https://github.com/hibernate/hibernate-orm",
        "https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations",
        "https://hub.docker.com/_/postgres",
        "https://www.google.com/"
    );

    protected static final String INVALID_LINK_MSG = "The link is not correct";

    protected void setAllUntrackedResponse(long chatId) {
        lenient().when(scrapperClient.fetchLinks(chatId)).thenReturn(createFetchLinksResponse());
    }

    protected void setAllTrackedResponse(long chatId, String... links) {
        lenient().when(scrapperClient.fetchLinks(chatId)).thenReturn(createFetchLinksResponse(links));
    }

    protected void setTrackingResponse(long chatId, String link) {
        lenient().when(scrapperClient.trackLink(chatId, link)).thenReturn(createTrackingResponse(link));
        lenient().when(scrapperClient.untrackLink(chatId, link)).thenReturn(createTrackingResponse(link));
    }

    protected void setAlreadyTrackingResponse(long chatId, String link) {
        lenient().when(scrapperClient.trackLink(chatId, link))
            .thenThrow(new ConflictException(createAlreadyTrackingResponse()));
    }

    protected void setUnsupportedResponse(long chatId, String link, String... domains) {
        lenient().when(scrapperClient.trackLink(chatId, link))
            .thenThrow(new BadRequestException(createUnsupportedResponse(link, domains)));
    }

    protected void setInvalidLinkResponse(long chatId, String link) {
        lenient().when(scrapperClient.trackLink(chatId, link))
            .thenThrow(new BadRequestException(createInvalidLinkResponse()));
        lenient().when(scrapperClient.untrackLink(chatId, link))
            .thenThrow(new BadRequestException(createInvalidLinkResponse()));
    }

    protected void setNotTrackingYetResponse(long chatId, String link) {
        lenient().when(scrapperClient.untrackLink(chatId, link))
            .thenThrow(new NotFoundException(createNotTrackingYetResponse()));
    }

    private ListLinksResponse createFetchLinksResponse(String... links) {
        return new ListLinksResponse(IntStream.range(0, links.length)
            .mapToObj(index -> new LinkResponse(index + 1, TestUtils.toUrl(links[index])))
            .toList(), links.length);
    }

    private LinkResponse createTrackingResponse(String link) {
        return new LinkResponse(new Random().nextInt(), TestUtils.toUrl(link));
    }

    private ApiErrorResponse createAlreadyTrackingResponse() {
        return new ApiErrorResponse(
            "Link is already tracking",
            "409",
            "LinkAlreadyTrackingException",
            "Link is already tracking",
            List.of("stacktrace")
        );
    }

    private ApiErrorResponse createUnsupportedResponse(String link, String... domains) {
        return new ApiErrorResponse(
            "Domain " + TestUtils.toUrl(link).getHost() + " is not supported yet. List of all supported domains:\n"
                + CommonUtils.joinEnumerated(Arrays.stream(domains).toList(), 1),
            "400"
        );
    }

    private ApiErrorResponse createInvalidLinkResponse() {
        return new ApiErrorResponse(
            "The link is not correct",
            "400"
        );
    }

    private ApiErrorResponse createNotTrackingYetResponse() {
        return new ApiErrorResponse(
            "The link is not tracked by this chat",
            "404",
            "NoSuchLinkException",
            "Cannot find link",
            List.of()
        );
    }
}
