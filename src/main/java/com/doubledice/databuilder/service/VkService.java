package com.doubledice.databuilder.service;

import com.doubledice.databuilder.bean.BeanBuilder;
import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.User;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ponomarev 16.08.2022
 */
@Service
@RequiredArgsConstructor
public class VkService {
    @Autowired
    @Lazy
    private BeanBuilder beanBuilder;
    @Autowired
    private final UserService userService;
    @Autowired
    private final AnalyticService analyticService;
    @Autowired
    private final GroupService groupService;
    @Autowired
    @Lazy
    VkApiClient vkApiClient;
    @Autowired
    @Lazy
    ServiceActor serviceActor;

    /**
     * @param vkLink - ссылка на сообщество вк
     * @param group  объект группы, который мы создали ранее
     * @return список User с основными данными, взятыми из АПИ (имя, фамилия, вк id, group которую мы обрабатываем)
     * @throws ClientException
     * @throws ApiException
     */
    public synchronized Set<User> getUsersByGroupLink(String vkLink, Group group) throws ClientException, ApiException {
        Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<User> groupUsers = new HashSet<>();
        int groupSize = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).execute().getCount();
        for (int i = 0; i < groupSize; i += 1000) {
//            List<User> currentUsers = userService.findAll();
            List<Integer> userIds = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).offset(i).execute().getItems();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            vkApiClient.users().get(serviceActor).userIds(String.valueOf(userIds)).execute().stream().forEach(s -> {
                //todo extract this into users list
                User user = userService.findByVkLink(s.getId().toString());
                if (user == null) {
                    user = new User();
                }
                user.setFirstName(s.getFirstName());
                user.setLastName(s.getLastName());
                user.setVkLink(s.getId().toString());
                user.setNewGroupIfNotExist(group);
                groupUsers.add(userService.addUser(user));
            });
        }
        return groupUsers;
    }

    /**
     * @param vkLink
     * @return название сообщества
     * @throws ClientException
     * @throws ApiException
     */
    public String getGroupNameByVKLink(String vkLink) throws ClientException, ApiException {
        Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
        return vkApiClient.groups().getByIdObjectLegacy(serviceActor).groupId(id.toString()).execute().get(0).getName();
    }

    public synchronized void scanExistingGroup(Group group, String vkLink) throws ClientException, ApiException {
        Set<User> dataGroupUserList = new HashSet<>(group.getVkUsers());
        Set<User> currentGroupUserList = new HashSet<>(getUsersByGroupLink(vkLink, group));
        Set<User> exitUsers = (Set<User>) ((HashSet<User>) dataGroupUserList).clone();
        exitUsers.removeAll(currentGroupUserList);
        Set<User> joinedUsers = (Set<User>) ((HashSet<User>) currentGroupUserList).clone();
        joinedUsers.removeAll(dataGroupUserList);
        if (!CollectionUtils.isEmpty(exitUsers) || !CollectionUtils.isEmpty(joinedUsers)) {
            Analytic analytic = new Analytic(group, exitUsers, joinedUsers);
            analyticService.addAnalytic(analytic);
        }
        group.setVkUsers(currentGroupUserList);
        groupService.addGroup(group);
    }
}
