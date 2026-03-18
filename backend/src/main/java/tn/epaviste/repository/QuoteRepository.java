package tn.epaviste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Quote;
import tn.epaviste.entity.RFQ;
import tn.epaviste.entity.User;
import tn.epaviste.enums.QuoteStatus;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByRfq(RFQ rfq);
    List<Quote> findByRfqOrderByCreatedAtAsc(RFQ rfq);
    List<Quote> findBySeller(User seller);
    List<Quote> findBySellerOrderByCreatedAtDesc(User seller);
    long countByRfq(RFQ rfq);
    long countBySellerAndStatus(User seller, QuoteStatus status);

    @Query("SELECT COUNT(DISTINCT q.rfq.id) FROM Quote q WHERE q.seller = :seller")
    long countDistinctRfqBySeller(@Param("seller") User seller);
}
