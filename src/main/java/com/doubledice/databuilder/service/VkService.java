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

import java.util.Collections;
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
     * @param vkLink - адрес страницы сообщества вк после "/"
     * @param group  объект группы, который мы создали ранее
     * @return список User с основными данными, взятыми из АПИ (имя, фамилия, вк id, group которую мы обрабатываем)
     * @throws ClientException
     * @throws ApiException
     */
    public Set<User> getUsersByGroupLink(String vkLink, Group group) throws ClientException, ApiException {
        Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Set<User> groupUsers = Collections.synchronizedSet(new HashSet<>());
        int groupSize = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).execute().getCount();
        synchronized (VkApiClient.class) {
//        List<User> allCurrentUsers = userService.findAll();
            for (int i = 0; i < groupSize; i += 100) {
                List<Integer> userIds = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).count(100).offset(i).execute().getItems();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                vkApiClient.users().get(serviceActor).userIds(String.valueOf(userIds)).execute().forEach(s -> {
                    User user = userService.findByVkLink(s.getId().toString());
//                    User user = findUserByVkLink(s.getId().toString(), allCurrentUsers);
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
        }
        return groupUsers;
    }

//    private User findUserByVkLink(String vkLink, List<User> allCurrentUsers) {
//        return allCurrentUsers.stream().filter(s -> s.getVkLink().equals(vkLink))
//                .findFirst()
//                .orElse(null);
//    }

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

    public void scanExistingGroup(Group group, String vkLink) throws ClientException, ApiException {
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
