package tn.epaviste.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Order;
import tn.epaviste.entity.User;
import tn.epaviste.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
    List<Order> findBySellerOrderByCreatedAtDesc(User seller);
    List<Order> findByBuyerOrSellerOrderByCreatedAtDesc(User buyer, User seller);
    long countByOrderStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.orderStatus = 'DELIVERED'")
    BigDecimal sumTotalGMV();

    @Query("SELECT COALESCE(SUM(o.commissionAmount), 0) FROM Order o WHERE o.orderStatus = 'DELIVERED'")
    BigDecimal sumTotalCommission();
}
