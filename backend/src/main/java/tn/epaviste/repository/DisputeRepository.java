package tn.epaviste.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.epaviste.entity.Dispute;
import tn.epaviste.enums.DisputeStatus;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);
}
