package com.doubledice.databuilder.repository;

import com.doubledice.databuilder.model.Analytic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ponomarev 16.07.2022
 */
public interface AnalyticRepository extends JpaRepository<Analytic, Long> {
}
