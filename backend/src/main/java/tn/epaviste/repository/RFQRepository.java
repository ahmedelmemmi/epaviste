package tn.epaviste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.RFQ;
import tn.epaviste.entity.User;
import tn.epaviste.enums.RFQStatus;

import java.util.List;

@Repository
public interface RFQRepository extends JpaRepository<RFQ, Long> {
    Page<RFQ> findByStatus(RFQStatus status, Pageable pageable);
    List<RFQ> findByBuyer(User buyer);
    List<RFQ> findByBuyerOrderByCreatedAtDesc(User buyer);
}
