package com.buildit.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantHireRequestRepository extends JpaRepository<PlantHireRequest, Long> {
}
