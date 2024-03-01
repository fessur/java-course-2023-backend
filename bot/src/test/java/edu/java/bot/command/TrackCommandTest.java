package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TestUtils;
import edu.java.bot.UpdateMock;
import edu.java.bot.service.command.TrackCommand;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TrackCommandTest extends LinksCommandsBaseTest {
    private TrackCommand trackCommand;
    private static final String DESCRIPTION = "Start tracking a link";
    private static final String TRACK_TWICE_MSG = "You are already tracking this link.";

    @BeforeEach
    public void setUp() {
        trackCommand = new TrackCommand(scrapperClient);
    }

    @Test
    public void testOneMessage() {
        Update update = new UpdateMock().withChat().withMessage("/track " + LINKS.getFirst()).build();
        setTrackingResponse(update.message().chat().id(), LINKS.getFirst());
        SendMessage sendMessage = trackCommand.process(update);
        TestUtils.checkMessage(sendMessage, generateTrackStartMsg(LINKS.getFirst()));
    }

    @Test
    public void testTwoMessages() {
        Update update1 = new UpdateMock().withChat().withMessage("/track " + LINKS.getFirst()).build();
        final long chatId = update1.message().chat().id();
        setTrackingResponse(chatId, LINKS.getFirst());
        SendMessage sendMessage1 = trackCommand.process(update1);
        TestUtils.checkMessage(sendMessage1, generateTrackStartMsg(LINKS.getFirst()));

        setTrackingResponse(chatId, LINKS.get(1));
        Update update2 = new UpdateMock().withChat(chatId).withMessage("/track " + LINKS.get(1)).build();
        SendMessage sendMessage2 = trackCommand.process(update2);
        TestUtils.checkMessage(sendMessage2, generateTrackStartMsg(LINKS.get(1)));
    }

    @Test
    public void testMessageTwice() {
        Update update1 = new UpdateMock().withChat().withMessage("/track " + LINKS.getFirst()).build();
        final long chatId = update1.message().chat().id();
        setTrackingResponse(chatId, LINKS.getFirst());
        SendMessage sendMessage1 = trackCommand.process(update1);
        TestUtils.checkMessage(sendMessage1, generateTrackStartMsg(LINKS.getFirst()));

        setAlreadyTrackingResponse(chatId, LINKS.getFirst());
        Update update2 = new UpdateMock().withChat(chatId).withMessage("/track " + LINKS.getFirst()).build();
        SendMessage sendMessage2 = trackCommand.process(update2);
        TestUtils.checkMessage(sendMessage2, TRACK_TWICE_MSG);
    }

    @Test
    public void testTwoSenders() {
        Update update1 = new UpdateMock().withChat().withMessage("/track " + LINKS.get(0)).build();
        Update update2 = new UpdateMock().withChat().withMessage("/track " + LINKS.get(1)).build();
        final long chatId1 = update1.message().chat().id();
        final long chatId2 = update2.message().chat().id();
        setTrackingResponse(chatId1, LINKS.get(0));
        setTrackingResponse(chatId2, LINKS.get(1));
        SendMessage sendMessage1 = trackCommand.process(update1);
        SendMessage sendMessage2 = trackCommand.process(update2);
        TestUtils.checkMessage(sendMessage1, generateTrackStartMsg(LINKS.get(0)));
        TestUtils.checkMessage(sendMessage2, generateTrackStartMsg(LINKS.get(1)));
    }

    @Test
    public void testUnsupported() {
        Update update1 = new UpdateMock().withChat().withMessage("/track " + LINKS.getFirst()).build();
        final long chatId = update1.message().chat().id();
        setUnsupportedResponse(chatId, LINKS.getFirst(), "github.com", "miro.com");
        SendMessage sendMessage1 = trackCommand.process(update1);
        TestUtils.checkMessage(sendMessage1, generateUnsupportedMessage(LINKS.getFirst(),"github.com", "miro.com"));
    }

    @Test
    public void testInvalid() {
        String invalidLink = "not-a-valid-link";
        Update update = new UpdateMock().withChat().withMessage("/track " + invalidLink).build();
        final long chatId = update.message().chat().id();
        setInvalidLinkResponse(chatId, invalidLink);
        SendMessage sendMessage = trackCommand.process(update);
        TestUtils.checkMessage(sendMessage, INVALID_LINK_MSG);
    }

    @Test
    public void testSupports() {
        assertThat(trackCommand.supports(new UpdateMock().withMessage("/track " + LINKS.getFirst())
            .build())).isTrue();
        assertThat(trackCommand.supports(new UpdateMock().withMessage("/untrack " + LINKS.getFirst())
            .build())).isFalse();
    }

    @Test
    public void testCommandAndDescription() {
        assertThat(trackCommand.command()).isNotNull().isEqualTo("track");
        assertThat(trackCommand.description()).isNotNull().isEqualTo(DESCRIPTION);
    }

    @Test
    public void testToApiCommand() {
        assertThat(trackCommand.toApiCommand()).isNotNull().isEqualTo(new BotCommand("track", DESCRIPTION));
    }

    private String generateTrackStartMsg(String link) {
        return String.format("Link %s is now being tracked.", link);
    }

    private String generateUnsupportedMessage(String link, String... supportedDomains) {
        return "Domain " + TestUtils.toUrl(link).getHost() + " is not supported yet. List of all supported domains:\n" +
            IntStream.range(0, supportedDomains.length).mapToObj(i -> (i + 1) + ". " + supportedDomains[i]).collect(
                Collectors.joining("\n"));
    }
}
