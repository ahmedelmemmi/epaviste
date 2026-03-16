package tn.epaviste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByOrderSellerIdOrderByCreatedAtDesc(Long sellerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.order.seller.id = :sellerId")
    Double findAverageRatingBySellerId(Long sellerId);
}
