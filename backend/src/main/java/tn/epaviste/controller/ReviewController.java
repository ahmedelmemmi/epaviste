package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.ReviewRequest;
import tn.epaviste.dto.response.ReviewResponse;
import tn.epaviste.service.ReviewService;

import java.util.List;

@Tag(name = "Reviews", description = "Buyers leave reviews for sellers after a completed order")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Submit a review",
            description = "Buyer submits a rating and comment for a seller after a completed order")
    @ApiResponse(responseCode = "200", description = "Review submitted",
            content = @Content(schema = @Schema(implementation = ReviewResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(@Valid @RequestBody ReviewRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(reviewService.submitReview(request, authentication.getName()));
    }

    @Operation(summary = "Get reviews for a seller",
            description = "Returns all reviews submitted for the specified seller")
    @ApiResponse(responseCode = "200", description = "List of reviews")
    @ApiResponse(responseCode = "404", description = "Seller not found")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ReviewResponse>> getSellerReviews(
            @Parameter(description = "Seller user ID") @PathVariable Long sellerId) {
        return ResponseEntity.ok(reviewService.getSellerReviews(sellerId));
    }
}
