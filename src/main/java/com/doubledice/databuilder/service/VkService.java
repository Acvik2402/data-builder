package com.doubledice.databuilder.service;

import com.doubledice.databuilder.bean.BeanBuilder;
import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.User;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ponomarev 16.08.2022
 */
@Service
@RequiredArgsConstructor
public class VkService {
    public static final int ITERATION_SIZE = 100;
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
    private VkApiClient vkApiClient;
    @Autowired
    @Lazy
    private ServiceActor serviceActor;

    /**
     * @param vkLink - адрес страницы сообщества вк после "/"
     * @return название сообщества
     * @throws ClientException
     * @throws ApiException
     */
    public String getGroupNameByVKLink(String vkLink) throws ClientException, ApiException {
        Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
        return vkApiClient.groups().getByIdObjectLegacy(serviceActor).groupId(id.toString()).execute().get(0).getName();
    }

    /**
     * @param group объект группы, который мы создали ранее
     * @return список User с основными данными, взятыми из АПИ (имя, фамилия, вк id, group которую мы обрабатываем)
     * @throws ClientException
     * @throws ApiException
     */
    public Set<User> getUsersByGroupLink(Group group) throws ClientException, ApiException {
        synchronized (VkApiClient.class) {
            Set<User> groupUsers = new HashSet<>();
            Integer id = vkApiClient.utils().resolveScreenName(serviceActor, group.getVkLink()).execute().getObjectId();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int groupSize = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).execute().getCount();

            for (int i = 0; i <= groupSize; i += ITERATION_SIZE) {
                List<String> userIds = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).count(ITERATION_SIZE).offset(i).execute().getItems().stream().map(String::valueOf).collect(Collectors.toList());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                groupUsers.addAll(getUsersByVkId(userIds, group));
//                userIds.clear();
            }
            return groupUsers;
        }
    }

    public void scanExistingGroup(Group group, String vkLink) throws ClientException, ApiException {
        List<String> dataGroupUserIdList = new ArrayList<>(group.getVkUsersIdLinks());
        List<String> currentGroupUserIdList = new ArrayList<>(getUsersIdLinksByGroupLink(vkLink));
        List<String> exitUsersId = new ArrayList<>(dataGroupUserIdList);
        exitUsersId.removeAll(currentGroupUserIdList);
        List<String> joinedUsersId = new ArrayList<>(currentGroupUserIdList);
        joinedUsersId.removeAll(dataGroupUserIdList);
        synchronized (this) {
            if (!CollectionUtils.isEmpty(exitUsersId) || !CollectionUtils.isEmpty(joinedUsersId)) {
                Analytic analytic = new Analytic(group, getUsersByVkId(exitUsersId, group), getUsersByVkId(joinedUsersId, group));
                analyticService.addAnalytic(analytic);
                group.setVkUsers(getUsersByVkId(currentGroupUserIdList, group));
                groupService.addGroup(group);
            }
        }
        dataGroupUserIdList.clear();
        currentGroupUserIdList.clear();
        exitUsersId.clear();
        joinedUsersId.clear();
    }

    private Set<String> getUsersIdLinksByGroupLink(String vkLink) throws ClientException, ApiException {
        synchronized (VkApiClient.class) {
            Set<String> groupUsersLinks = new HashSet<>();
            Integer id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            int groupSize = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).execute().getCount();

            for (int i = 0; i <= groupSize; i += ITERATION_SIZE) {
                groupUsersLinks.addAll(vkApiClient.groups().getMembers(serviceActor).groupId(id.toString()).count(ITERATION_SIZE).offset(i).execute().getItems().stream().map(String::valueOf).collect(Collectors.toList()));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return groupUsersLinks;
        }
    }

    private Set<User> getUsersByVkId(List<String> userIds, Group group) throws ClientException, ApiException {
        synchronized (VkApiClient.class) {
            Set<User> groupUsers = new HashSet<>();
            for (List<String> iterSublist : ListUtils.partition(userIds, ITERATION_SIZE)) {
                vkApiClient.users().get(serviceActor).userIds(iterSublist).lang(Lang.RU).execute().forEach(s -> {
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
    }
}
