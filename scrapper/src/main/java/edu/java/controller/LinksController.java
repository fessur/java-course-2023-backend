package edu.java.controller;

import edu.java.controller.buckets.Limiter;
import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.ApiErrorResponse;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import edu.java.service.LinkService;
import edu.java.service.model.Link;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@Tag(name = "Links", description = "API for working with links")
public class LinksController extends BaseController {
    private final LinkService linkService;
    private final Limiter limiter;

    public LinksController(LinkService linkService, Limiter limiter) {
        this.linkService = linkService;
        this.limiter = limiter;
    }

    @GetMapping
    @Operation(summary = "Get all tracking links")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully got links", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ListLinksResponse.class))
        }),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "404", description = "Chat not found", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> getLinks(@RequestHeader("Tg-Chat-Id") long chatId, HttpServletRequest request) {
        if (limiter.tryConsume(request.getRemoteAddr())) {
            List<LinkResponse> links = linkService.listAll(chatId).stream().map(link -> new LinkResponse(
                link.getId(), link.getUrl())).toList();
            return ResponseEntity.ok().body(new ListLinksResponse(links, links.size()));
        } else {
            return createTooManyRequests();
        }
    }

    @PostMapping
    @Operation(summary = "Add tracking link", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true, content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = AddLinkRequest.class))
    }
    ))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Link successfully added", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
        }),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "404", description = "Chat not found", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "409", description = "Link is already tracking", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> addLink(
        @RequestHeader("Tg-Chat-Id") long chatId,
        @Valid @RequestBody AddLinkRequest addLinkRequest,
        BindingResult bindingResult,
        HttpServletRequest request
    ) {
        if (limiter.tryConsume(request.getRemoteAddr())) {
            if (bindingResult.hasErrors()) {
                return createBadRequestResponse(bindingResult);
            }
            Link addedLink = linkService.add(addLinkRequest.link(), chatId);
            return ResponseEntity.ok().body(new LinkResponse(addedLink.getId(), addedLink.getUrl()));
        } else {
            return createTooManyRequests();
        }
    }

    @DeleteMapping
    @Operation(summary = "Remove link tracking", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true, content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = RemoveLinkRequest.class))
    }
    ))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully removed link", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))
        }),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        }),
        @ApiResponse(responseCode = "404",
                     description = "Chat not found or doesn't contain the specified link",
                     content = {
                         @Content(mediaType = "application/json",
                                  schema = @Schema(implementation = ApiErrorResponse.class))
                     }),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        })
    })
    public ResponseEntity<?> deleteLink(
        @RequestHeader("Tg-Chat-Id") long chatId,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest,
        BindingResult bindingResult,
        HttpServletRequest request
    ) {
        if (limiter.tryConsume(request.getRemoteAddr())) {
            if (bindingResult.hasErrors()) {
                return createBadRequestResponse(bindingResult);
            }
            Link deleted = linkService.remove(removeLinkRequest.link(), chatId);
            return ResponseEntity.ok().body(new LinkResponse(deleted.getId(), deleted.getUrl()));
        } else {
            return createTooManyRequests();
        }
    }
}
