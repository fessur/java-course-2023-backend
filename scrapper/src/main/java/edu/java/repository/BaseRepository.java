package edu.java.repository;

import com.google.gson.Gson;
import edu.java.configuration.ApplicationConfig;
import edu.java.service.domain.Chat;
import edu.java.service.exception.DBException;
import edu.java.service.exception.NoSuchChatException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public abstract class BaseRepository {
    private final Gson gson = new Gson();
    private final String databasePath;
    private static final String ERROR_MESSAGE = "Unable to load database.";

    public BaseRepository(ApplicationConfig applicationConfig) {
        this.databasePath = applicationConfig.localDbPath();
    }

    protected Database readDatabase() {
        try (FileReader reader = new FileReader(databasePath, StandardCharsets.UTF_8)) {
            Database database = gson.fromJson(reader, Database.class);
            return database != null ? database : new Database(1, new ArrayList<>());
        } catch (IOException e) {
            throw new DBException(ERROR_MESSAGE, e);
        }
    }

    protected void writeDatabase(Database database) {
        try (FileWriter writer = new FileWriter(databasePath, StandardCharsets.UTF_8)) {
            gson.toJson(database, writer);
        } catch (IOException e) {
            throw new DBException(ERROR_MESSAGE, e);
        }
    }

    protected Chat findChat(Database database, long chatId) {
        Optional<Chat> optionalChat = database.getChats().stream().filter(c -> c.id() == chatId).findFirst();
        if (optionalChat.isEmpty()) {
            throw new NoSuchChatException("Cannot find chat " + chatId);
        }
        return optionalChat.get();
    }
}
