package tn.epaviste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Quote;
import tn.epaviste.entity.RFQ;
import tn.epaviste.entity.User;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findByRfq(RFQ rfq);
    List<Quote> findByRfqOrderByCreatedAtAsc(RFQ rfq);
    List<Quote> findBySeller(User seller);
    List<Quote> findBySellerOrderByCreatedAtDesc(User seller);
    long countByRfq(RFQ rfq);
}
