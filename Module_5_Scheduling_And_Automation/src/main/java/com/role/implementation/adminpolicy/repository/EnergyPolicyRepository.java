package com.role.implementation.adminpolicy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.role.implementation.adminpolicy.model.EnergyPolicy;

@Repository
public interface EnergyPolicyRepository extends JpaRepository<EnergyPolicy, Long> {

    /**
     * Fetch all enabled admin energy policies
     * Used by EnergyPolicyService scheduler
     */
    List<EnergyPolicy> findByEnabledTrue();
}
