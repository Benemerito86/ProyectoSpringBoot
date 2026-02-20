package com.tuorg.saas_core.repository;

import com.tuorg.saas_core.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
