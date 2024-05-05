package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TestUtils;
import edu.java.bot.UpdateMock;
import edu.java.bot.client.dto.ApiErrorResponse;
import edu.java.bot.client.exception.ConflictException;
import edu.java.bot.service.command.StartCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

public class StartCommandTest extends LinksCommandsBaseTest {
    private StartCommand startCommand;
    private final static String WELCOME = "Hello! Welcome to our bot!";
    private final static String WELCOME_AGAIN =
        "You are already working with our bot. Use /help to see a list of all possible commands";
    private final static String DESCRIPTION = "Start working with the bot";

    @BeforeEach
    public void setUp() {
        startCommand = new StartCommand(scrapperClient);
    }

    @Test
    public void testOneMessage() {
        Update update = new UpdateMock().withChat().build();

        SendMessage sendMessage = startCommand.process(update);
        TestUtils.checkMessage(sendMessage, WELCOME);

    }

    @Test
    public void testTwoMessages() {
        Update update1 = new UpdateMock().withChat().build();
        final long chatId = update1.message().chat().id();
        SendMessage sendMessage1 = startCommand.process(update1);
        TestUtils.checkMessage(sendMessage1, WELCOME);

        Update update2 = new UpdateMock().withChat(chatId).build();
        doThrow(new ConflictException(createConflictErrorResponse())).when(scrapperClient).registerChat(chatId);
        SendMessage sendMessage2 = startCommand.process(update2);
        TestUtils.checkMessage(sendMessage2, WELCOME_AGAIN);
    }

    @Test
    public void testTwoSenders() {
        Update updateFrom1 = new UpdateMock().withChat().build();
        SendMessage sendMessage1 = startCommand.process(updateFrom1);
        TestUtils.checkMessage(sendMessage1, WELCOME);

        Update updateFrom2 = new UpdateMock().withChat().build();
        SendMessage sendMessage2 = startCommand.process(updateFrom2);
        TestUtils.checkMessage(sendMessage2, WELCOME);
    }

    @Test
    public void testTooManyRequests() {
       Update update = new UpdateMock().withChat().build();
       setTooManyRequests(update.message().chat().id());
       SendMessage sendMessage = startCommand.process(update);
       TestUtils.checkMessage(sendMessage, TOO_MANY_REQUESTS_MSG);
    }

    @Test
    public void testSupports() {
        assertThat(startCommand.supports(new UpdateMock().withMessage("/start").build())).isTrue();
        assertThat(startCommand.supports(new UpdateMock().withMessage("/help").build())).isFalse();
    }

    @Test
    public void testCommandAndDescription() {
        assertThat(startCommand.command()).isNotNull().isEqualTo("start");
        assertThat(startCommand.description()).isNotNull().isEqualTo(DESCRIPTION);
    }

    @Test
    public void testToApiCommand() {
        assertThat(startCommand.toApiCommand()).isNotNull().isEqualTo(new BotCommand("start", DESCRIPTION));
    }

    private ApiErrorResponse createConflictErrorResponse() {
        return new ApiErrorResponse(
            "Chat is already registered",
            "409",
            "ChatAlreadyRegisteredException",
            "Chat is already registered",
            List.of("stacktrace")
        );
    }
}
