package tn.epaviste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Order;
import tn.epaviste.entity.Payment;
import tn.epaviste.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByOrderSellerOrderByCreatedAtDesc(User seller);
}
