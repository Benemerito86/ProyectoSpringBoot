package com.tuorg.saas_core.repository;

import com.tuorg.saas_core.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query("SELECT s FROM Subscription s " +
            "WHERE s.status = com.tuorg.saas_core.model.SubscriptionStatus.ACTIVE " +
            "AND s.nextBillingAt <= CURRENT_TIMESTAMP")
    List<Subscription> findPendientesRenovacion();


    List<Subscription> findByUserIdOrderByNextBillingAt(Long userId);

}
