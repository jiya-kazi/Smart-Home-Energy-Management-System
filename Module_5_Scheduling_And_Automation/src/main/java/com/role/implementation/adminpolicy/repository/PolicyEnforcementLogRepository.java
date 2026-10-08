package com.role.implementation.adminpolicy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.role.implementation.adminpolicy.model.PolicyEnforcementLog;

@Repository
public interface PolicyEnforcementLogRepository
        extends JpaRepository<PolicyEnforcementLog, Long> {
}
