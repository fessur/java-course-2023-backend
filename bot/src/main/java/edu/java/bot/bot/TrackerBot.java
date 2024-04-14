package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.configuration.props.ApplicationConfig;
import edu.java.bot.service.command.Command;
import io.micrometer.core.instrument.Counter;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class TrackerBot extends TelegramBot {
    private final List<Command> commands;
    private final Counter processedMessages;

    public TrackerBot(
        ApplicationConfig applicationConfig,
        List<Command> commands,
        @Qualifier("processedMessages") Counter processedMessages
    ) {
        super(applicationConfig.telegramToken());
        this.commands = commands;
        this.processedMessages = processedMessages;
    }

    @PostConstruct
    public void start() {
        List<BotCommand> commandMenu = commands.stream().map(Command::toApiCommand).toList();
        execute(new SetMyCommands(commandMenu.toArray(new BotCommand[0])));
        setUpdatesListener(updates -> {
            updates.forEach(this::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {
        commands.stream()
            .filter(cmd -> cmd.supports(update))
            .findAny()
            .ifPresentOrElse(
                cmd -> execute(cmd.process(update)),
                () -> processUnrecognizedCommand(update)
            );
        processedMessages.increment();
    }

    private void processUnrecognizedCommand(Update update) {
        Optional.ofNullable(update.message()).ifPresent(message -> execute(new SendMessage(
            message.chat().id(),
            "This command is not supported.\nUse /help to see a list of all possible commands."
        )));
    }
}
