package edu.java.repository.jpa;

import edu.java.service.model.jpa.JpaChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRepository extends JpaRepository<JpaChat, Long> {
}
