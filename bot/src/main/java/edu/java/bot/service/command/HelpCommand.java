package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class HelpCommand extends Command {
    List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String command() {
        return "help";
    }

    @Override
    public String description() {
        return "Show all possible commands";
    }

    @Override
    public SendMessage process(Update update) {
        return new SendMessage(
            update.message().chat().id(),
            "List of all possible commands:\n" +
                "1. " + this + "\n" +
                IntStream.range(0, commands.size())
                    .mapToObj(index -> (index + 2) + ". " + commands.get(index).toString())
                    .collect(Collectors.joining("\n"))
        );
    }
}
