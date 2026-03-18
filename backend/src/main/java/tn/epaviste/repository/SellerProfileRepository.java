package tn.epaviste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.SellerProfile;
import tn.epaviste.entity.User;

import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUser(User user);
    Optional<SellerProfile> findByUserId(Long userId);
    Page<SellerProfile> findByVerifiedFalse(Pageable pageable);
    long countByVerifiedTrue();
}
