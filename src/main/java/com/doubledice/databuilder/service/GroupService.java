package com.doubledice.databuilder.service;

import com.doubledice.databuilder.model.Group;

import java.util.List;

/**
 * @author ponomarev 17.07.2022
 */
public interface GroupService {

    Group addGroup(Group group);

    Group findGroupByLink(String vkLink);

    Group findOrCreateGroupByLink(String vkLink);

    List<Group> findAll();

    void deleteById(Long id);

    Group findGroup(Long id);
}
