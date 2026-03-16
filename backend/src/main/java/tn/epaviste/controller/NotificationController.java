package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.response.NotificationResponse;
import tn.epaviste.service.NotificationService;

@Tag(name = "Notifications", description = "In-app notifications for users")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get my notifications",
            description = "Returns paginated notifications for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Page of notifications")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @Parameter(description = "Pagination (page, size, sort)") @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getUserNotifications(authentication.getName(), pageable));
    }

    @Operation(summary = "Mark a notification as read")
    @ApiResponse(responseCode = "200", description = "Notification marked as read",
            content = @Content(schema = @Schema(implementation = NotificationResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "404", description = "Notification not found")
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(notificationService.markAsRead(id, authentication.getName()));
    }

    @Operation(summary = "Mark all notifications as read",
            description = "Marks all unread notifications for the authenticated user as read")
    @ApiResponse(responseCode = "204", description = "All notifications marked as read")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
