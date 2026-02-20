package com.tuorg.saas_core.repository;

import com.tuorg.saas_core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
