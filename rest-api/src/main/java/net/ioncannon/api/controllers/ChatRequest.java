package net.ioncannon.api.controllers;

public record ChatRequest(
        String userMessage,
        String conversationId
) {
}
