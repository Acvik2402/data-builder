package com.doubledice.databuilder.service.impl;

import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.repository.AnalyticRepository;
import com.doubledice.databuilder.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author ponomarev 19.07.2022
 */

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticService {

    private final AnalyticRepository analyticRepository;

    @Override
    @Transactional
    public Analytic addAnalytic(Analytic analytic) {
        return analyticRepository.save(analytic);
    }

    @Override
    public List<Analytic> findAll() {
        return analyticRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        analyticRepository.deleteById(id);
    }
}
