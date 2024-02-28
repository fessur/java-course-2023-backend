package edu.java.client.dto;

import java.util.List;

public record StackOverflowPostResponse(List<StackOverflowPostInnerResponse> items) {}
