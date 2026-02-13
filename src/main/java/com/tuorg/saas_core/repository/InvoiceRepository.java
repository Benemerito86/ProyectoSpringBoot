package com.tuorg.saas_core.repository;

import com.tuorg.saas_core.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
      SELECT i FROM Invoice i
      WHERE i.subscription.user.id = :userId
        AND i.issueAt BETWEEN :desde AND :hasta
        AND i.totalCents >= :minTotalCents
      ORDER BY i.issueAt DESC
    """)
    List<Invoice> filtrarFacturas(Long userId, OffsetDateTime desde, OffsetDateTime hasta, Long minTotalCents);
}
