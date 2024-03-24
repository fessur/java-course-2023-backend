package edu.java.service;

public interface ChatService {
    void register(long chatId);

    void unregister(long chatId);

    String ALREADY_REGISTERED_MESSAGE = "Chat is already registered";

    String NOT_REGISTERED_MESSAGE = "Chat is not registered";
}
