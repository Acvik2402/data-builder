package com.doubledice.databuilder.service;

import com.doubledice.databuilder.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author ponomarev 31.08.2022
 */
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final GroupService groupService;
    private final VkService vkService;

    /**
     * задача автоматического сканирования имеющихся групп раз в сутки
     */
    @Scheduled(fixedRate = 86400000)
    public void scheduledGroupScan() {
        List<Group> groupList = Optional.of(groupService.findAll()).orElse(new ArrayList<>());
        for (Group group : groupList) {
            try {
                vkService.scanExistingGroup(group, group.getVkLink());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
