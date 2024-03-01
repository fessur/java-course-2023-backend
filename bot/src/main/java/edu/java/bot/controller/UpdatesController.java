package edu.java.bot.controller;

import edu.java.bot.controller.dto.ApiErrorResponse;
import edu.java.bot.controller.dto.LinkUpdateRequest;
import edu.java.bot.service.BotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@Tag(name = "Updates", description = "Api for link updates")
public class UpdatesController {
    private final BotService botService;

    public UpdatesController(BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    @Operation(summary = "Send update", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true, content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LinkUpdateRequest.class))
        }
    ))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Update processed", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid Request Parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> sendUpdate(
        @Valid @RequestBody LinkUpdateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                    bindingResult.getAllErrors().getFirst().getDefaultMessage(),
                    "400",
                    "",
                    "",
                    List.of()
                ));
        }
        botService.sendMessages(request.getTgChatIds(), request.getUrl(), request.getDescription());
        return ResponseEntity.ok().build();
    }
}
