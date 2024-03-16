package edu.java.repository.dto;

import java.util.Date;

public record Link(long id, String url, Date lastCheckTime, Date createdAt) {
}
