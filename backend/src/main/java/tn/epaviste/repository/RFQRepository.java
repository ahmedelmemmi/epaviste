package tn.epaviste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT r FROM RFQ r WHERE r.status = :status " +
           "AND (:carBrand IS NULL OR LOWER(r.carBrand) LIKE LOWER(CONCAT('%', :carBrand, '%'))) " +
           "AND (:carModel IS NULL OR LOWER(r.carModel) LIKE LOWER(CONCAT('%', :carModel, '%'))) " +
           "AND (:partCategory IS NULL OR LOWER(r.partCategory) LIKE LOWER(CONCAT('%', :partCategory, '%'))) " +
           "AND (:location IS NULL OR LOWER(r.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<RFQ> findByStatusAndFilters(@Param("status") RFQStatus status,
                                      @Param("carBrand") String carBrand,
                                      @Param("carModel") String carModel,
                                      @Param("partCategory") String partCategory,
                                      @Param("location") String location,
                                      Pageable pageable);
}
