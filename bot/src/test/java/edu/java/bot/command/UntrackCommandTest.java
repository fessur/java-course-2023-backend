package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TestUtils;
import edu.java.bot.UpdateMock;
import edu.java.bot.service.command.UntrackCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class UntrackCommandTest extends LinksCommandsBaseTest {
    private UntrackCommand untrackCommand;
    private static final String UNTRACK_EMPTY_MSG = "You are not tracking this link yet";
    private static final String DESCRIPTION = "Stop tracking a link";

    @BeforeEach
    public void setUp() {
        untrackCommand = new UntrackCommand(scrapperClient);
    }

    @Test
    public void testEmpty() {
        Update update = new UpdateMock().withChat().withMessage("/untrack " + LINKS.getFirst()).build();
        setNotTrackingYetResponse(update.message().chat().id(), LINKS.getFirst());
        SendMessage sendMessage = untrackCommand.process(update);
        TestUtils.checkMessage(sendMessage, UNTRACK_EMPTY_MSG);
    }

    @Test
    public void testOneMessage() {
        Update update = new UpdateMock().withChat().withMessage("/untrack " + LINKS.getFirst()).build();
        final long chatId = update.message().chat().id();
        setTrackingResponse(chatId, LINKS.getFirst());
        SendMessage sendMessage = untrackCommand.process(update);
        TestUtils.checkMessage(sendMessage, generateUntrackedMessage(LINKS.getFirst()));
    }

    @Test
    public void testTwoMessages() {
        Update update1 = new UpdateMock().withChat().withMessage("/untrack " + LINKS.getFirst()).build();
        final long chatId = update1.message().chat().id();
        setTrackingResponse(chatId, LINKS.getFirst());
        SendMessage sendMessage1 = untrackCommand.process(update1);
        TestUtils.checkMessage(sendMessage1, generateUntrackedMessage(LINKS.getFirst()));

        setTrackingResponse(chatId, LINKS.get(1));
        Update update2 = new UpdateMock().withChat(chatId).withMessage("/untrack " + LINKS.get(1)).build();
        SendMessage sendMessage2 = untrackCommand.process(update2);
        TestUtils.checkMessage(sendMessage2, generateUntrackedMessage(LINKS.get(1)));
    }

    @Test
    public void testTwoSenders() {
        Update update1 = new UpdateMock().withChat().withMessage("/untrack " + LINKS.get(0)).build();
        Update update2 = new UpdateMock().withChat().withMessage("/untrack " + LINKS.get(1)).build();
        final long chatId1 = update1.message().chat().id();
        final long chatId2 = update2.message().chat().id();
        setTrackingResponse(chatId1, LINKS.get(0));
        setTrackingResponse(chatId2, LINKS.get(1));
        SendMessage sendMessage1 = untrackCommand.process(update1);
        SendMessage sendMessage2 = untrackCommand.process(update2);
        TestUtils.checkMessage(sendMessage1, generateUntrackedMessage(LINKS.get(0)));
        TestUtils.checkMessage(sendMessage2, generateUntrackedMessage(LINKS.get(1)));
    }

    @Test
    public void testInvalid() {
        String invalidLink = "not-a-valid-link";
        Update update = new UpdateMock().withChat().withMessage("/untrack " + invalidLink).build();
        final long chatId = update.message().chat().id();
        setInvalidLinkResponse(chatId, invalidLink);
        SendMessage sendMessage = untrackCommand.process(update);
        TestUtils.checkMessage(sendMessage, INVALID_LINK_MSG);
    }

    @Test
    public void testSupports() {
        assertThat(untrackCommand.supports(new UpdateMock().withMessage("/untrack " + LINKS.getFirst())
            .build())).isTrue();
        assertThat(untrackCommand.supports(new UpdateMock().withMessage("/track " + LINKS.getFirst())
            .build())).isFalse();
    }

    @Test
    public void testCommandAndDescription() {
        assertThat(untrackCommand.command()).isNotNull().isEqualTo("untrack");
        assertThat(untrackCommand.description()).isNotNull().isEqualTo(DESCRIPTION);
    }

    @Test
    public void testToApiCommand() {
        assertThat(untrackCommand.toApiCommand()).isNotNull().isEqualTo(new BotCommand("untrack", DESCRIPTION));
    }

    private String generateUntrackedMessage(String link) {
        return String.format("Link %s is no longer being tracked.", link);
    }
}
