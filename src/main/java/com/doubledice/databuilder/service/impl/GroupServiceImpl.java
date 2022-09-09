package com.doubledice.databuilder.service.impl;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.repository.GroupRepository;
import com.doubledice.databuilder.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Transactional
    public Group addGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public Group findGroupByLink(String vkLink) {
        return groupRepository.findGroupByVkLink(vkLink).orElse(null);
    }

    @Override
    @Transactional
    public Group findOrCreateGroupByLink(String vkLink) {
        return groupRepository.findGroupByVkLink(vkLink).orElse(new Group(vkLink));
    }

    @Override
    @Transactional
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Group findGroup(Long id) {
        return groupRepository.findById(id).orElse(null);
    }
}
