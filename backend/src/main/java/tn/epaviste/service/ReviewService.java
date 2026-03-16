package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.ReviewRequest;
import tn.epaviste.dto.response.ReviewResponse;
import tn.epaviste.entity.Order;
import tn.epaviste.entity.Review;
import tn.epaviste.entity.SellerProfile;
import tn.epaviste.entity.User;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.OrderRepository;
import tn.epaviste.repository.ReviewRepository;
import tn.epaviste.repository.SellerProfileRepository;
import tn.epaviste.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Transactional
    public ReviewResponse submitReview(ReviewRequest request, String email) {
        User reviewer = getUserByEmail(email);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        if (!order.getBuyer().getId().equals(reviewer.getId())) {
            throw new UnauthorizedException("Only the buyer can review this order");
        }

        Review review = Review.builder()
                .order(order)
                .reviewer(reviewer)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        Review saved = reviewRepository.save(review);

        // Update seller's average rating
        Long sellerId = order.getSeller().getId();
        Double avgRating = reviewRepository.findAverageRatingBySellerId(sellerId);
        sellerProfileRepository.findByUserId(sellerId).ifPresent(profile -> {
            profile.setRating(avgRating != null ? avgRating : 0.0);
            sellerProfileRepository.save(profile);
        });

        return toResponse(saved);
    }

    public List<ReviewResponse> getSellerReviews(Long sellerId) {
        return reviewRepository.findByOrderSellerIdOrderByCreatedAtDesc(sellerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .reviewerId(review.getReviewer().getId())
                .reviewerName(review.getReviewer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
