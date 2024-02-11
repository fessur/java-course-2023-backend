package edu.java.bot.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.domain.Chat;
import edu.java.bot.exception.DBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
public class ChatRepository {
    private final Gson gson = new Gson();
    private final String databasePath;

    public ChatRepository(ApplicationConfig applicationConfig) {
        this.databasePath = applicationConfig.localDbPath();
    }

    public void save(Chat chat) {
        try {
            List<Chat> chats = readChats();
            chats.add(chat);
            writeChats(chats);
        } catch (IOException e) {
            throw new DBException("Unable to load database.", e);
        }
    }

    public Optional<Chat> findById(long id) {
        try {
            List<Chat> chats = readChats();
            return chats.stream().filter(chat -> chat.getId() == id).findFirst();
        } catch (IOException e) {
            throw new DBException("Unable to load database.", e);
        }
    }

    private List<Chat> readChats() throws IOException {
        try (FileReader reader = new FileReader(databasePath, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<Chat>>(){}.getType();
            List<Chat> chats = gson.fromJson(reader, listType);
            return chats != null ? chats : new ArrayList<>();
        }
    }

    private void writeChats(List<Chat> chats) throws IOException {
        try (FileWriter writer = new FileWriter(databasePath, StandardCharsets.UTF_8)) {
            gson.toJson(chats, writer);
        }
    }
}
