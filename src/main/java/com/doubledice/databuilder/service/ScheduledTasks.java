package com.doubledice.databuilder.service;

import com.doubledice.databuilder.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ponomarev 31.08.2022
 */
@Component
@RequiredArgsConstructor
public class ScheduledTasks implements Runnable {
    private final GroupService groupService;
    private final VkService vkService;

    /**
     * задача автоматического сканирования имеющихся групп раз в сутки
     */
    @Scheduled(fixedRate = 86400000)
    @Override
    public void run() {
        List<Group> groupList = groupService.findAll();
        for (Group group : groupList) {
            try {
                vkService.scanExistingGroup(group, group.getVkLink());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
