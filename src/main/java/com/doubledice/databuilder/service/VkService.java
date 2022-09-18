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

import java.util.*;
import java.util.stream.Collectors;

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
        Set<User> groupUsers = Collections.synchronizedSet(new HashSet<>());
        synchronized (VkApiClient.class) {
            Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int groupSize = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).execute().getCount();

            for (int i = 0; i < groupSize; i += 100) {
                List<String> userIds = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).count(100).offset(i).execute().getItems().stream().map(String::valueOf).collect(Collectors.toList());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                vkApiClient.users().get(serviceActor).userIds(userIds).execute().forEach(s -> {
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

    public void scanExistingGroup(Group group, String vkLink) throws ClientException, ApiException {
        List<User> dataGroupUserList = Collections.synchronizedList(new ArrayList<>(group.getVkUsers()));
        List<User> currentGroupUserList = Collections.synchronizedList(new ArrayList<>(getUsersByGroupLink(vkLink, group)));
        List<User> exitUsers = Collections.synchronizedList(new ArrayList<>(dataGroupUserList));
        exitUsers.removeAll(currentGroupUserList);
        List<User> joinedUsers = Collections.synchronizedList(new ArrayList<>(currentGroupUserList));
        joinedUsers.removeAll(dataGroupUserList);
        if (!CollectionUtils.isEmpty(exitUsers) || !CollectionUtils.isEmpty(joinedUsers)) {
            Analytic analytic = new Analytic(group, new HashSet<>(exitUsers), new HashSet<>(joinedUsers));
            analyticService.addAnalytic(analytic);
        }
        group.setVkUsers(new HashSet<>(currentGroupUserList));
        groupService.addGroup(group);
    }
}
