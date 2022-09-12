package com.doubledice.databuilder.service;

import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.repository.AnalyticRepository;
import com.doubledice.databuilder.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ponomarev 31.08.2022
 */
@Component
@RequiredArgsConstructor
@EnableAsync
public class ScheduledTasks {
    public static final int ANALYTIC_LIFE_CYCLE = 30;
    private final GroupRepository groupRepository;
    private final AnalyticRepository analyticRepository;
    private final VkService vkService;

    /**
     * удаление старых элементов analytic раз в день создание новых
     */
    @Scheduled(fixedRate = 86400000)
    @Async
    public void start() {
        List<Group> groupList = groupRepository.findAll();
        for (Group group : groupList) {
            try {
                vkService.scanExistingGroup(group, group.getVkLink());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Scheduled(fixedRate = 86400000)
    @Async
    public void clearAnalyticOldData() {
        List<Analytic> analyticList = analyticRepository.findAll();
        analyticList.stream().filter(s -> getDateDiff(s.getDate(), new Date(), TimeUnit.DAYS) > ANALYTIC_LIFE_CYCLE).forEach(analyticRepository::delete);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
