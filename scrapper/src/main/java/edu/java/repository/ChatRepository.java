package edu.java.repository;

import edu.java.configuration.ApplicationConfig;
import edu.java.service.domain.Chat;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ChatRepository extends BaseRepository {
    public ChatRepository(ApplicationConfig applicationConfig) {
        super(applicationConfig);
    }

    public void save(Chat chat) {
        Database database = readDatabase();
        database.getChats().add(chat);
        writeDatabase(database);
    }

    public Optional<Chat> findById(long id) {
        Database database = readDatabase();
        return database.getChats().stream().filter(chat -> chat.id() == id).findFirst();
    }

    public void delete(long id) {
        Database database = readDatabase();
        Chat chat = findChat(database, id);
        database.getChats().remove(chat);
        writeDatabase(database);
    }
}
