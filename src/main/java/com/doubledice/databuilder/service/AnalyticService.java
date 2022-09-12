package com.doubledice.databuilder.service;

import com.doubledice.databuilder.model.Analytic;

import java.util.List;

/**
 * @author ponomarev 17.07.2022
 */
public interface AnalyticService {

    Analytic addAnalytic(Analytic analytic);

    List<Analytic> findAll();

    void deleteById(Long id);
}
