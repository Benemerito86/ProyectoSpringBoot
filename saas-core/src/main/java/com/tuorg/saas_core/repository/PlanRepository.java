package com.tuorg.saas_core.repository;

import com.tuorg.saas_core.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
