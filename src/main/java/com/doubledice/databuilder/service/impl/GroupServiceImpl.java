package com.doubledice.databuilder.service.impl;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.repository.GroupRepository;
import com.doubledice.databuilder.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ponomarev 17.07.2022
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public Group addGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group findGroupByLink(String vkLink) {
        return groupRepository.findGroupByVkLink(vkLink).orElse(null);
    }
    @Override
    public Group findOrCreateGroupByLink(String vkLink) {
        return groupRepository.findGroupByVkLink(vkLink).orElse(new Group(vkLink));
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    public Group findGroup(Long id) {
        return groupRepository.findById(id).orElse(null);
    }
}
