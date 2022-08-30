package com.doubledice.databuilder.controller;

import com.doubledice.databuilder.dto.AnalyticDTO;
import com.doubledice.databuilder.dto.GroupDTO;
import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.service.AnalyticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ponomarev 24.07.2022
 */
@Controller()
@AllArgsConstructor

@RequestMapping("/analytic")
public class AnalyticController {
    private AnalyticService analyticService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/analytics")
    public String findAll(Model model) {
        List<Analytic> analytics = analyticService.findAll();
        model.addAttribute("analytics", analytics);
        return "analytic-list";
    }
}
