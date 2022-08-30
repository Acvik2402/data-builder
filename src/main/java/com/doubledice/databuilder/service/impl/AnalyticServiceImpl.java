package com.doubledice.databuilder.service.impl;

import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.repository.AnalyticRepository;
import com.doubledice.databuilder.service.AnalyticService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ponomarev 19.07.2022
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {

    @Autowired
    AnalyticRepository analyticRepository;

    @Override
    public Analytic addAnalytic(Analytic analytic) {
        return analyticRepository.save(analytic);
    }

    @Override
    public List<Analytic> findAll() {
        return analyticRepository.findAll();
    }
}
