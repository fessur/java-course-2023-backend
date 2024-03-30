package edu.java.controller;

import edu.java.controller.buckets.Limiter;
import edu.java.controller.dto.ApiErrorResponse;
import edu.java.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat/{id}")
@Tag(name = "Chats", description = "API for telegram chats")
public class ChatController extends BaseController {
    private final ChatService chatService;
    private final Limiter limiter;

    public ChatController(ChatService chatService, Limiter limiter) {
        this.chatService = chatService;
        this.limiter = limiter;
    }

    @PostMapping
    @Operation(summary = "Register chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat successfully registered", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "409", description = "Chat is already registered", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> register(@PathVariable("id") long id, HttpServletRequest request) {
        if (limiter.tryConsume(request.getRemoteAddr())) {
            chatService.register(id);
            return ResponseEntity.ok().build();
        } else {
            return createTooManyRequests();
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat successfully deleted", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "404", description = "Chat is not registered", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> delete(@PathVariable("id") long id, HttpServletRequest request) {
        if (limiter.tryConsume(request.getRemoteAddr())) {
            chatService.unregister(id);
            return ResponseEntity.ok().build();
        } else {
            return createTooManyRequests();
        }
    }
}
