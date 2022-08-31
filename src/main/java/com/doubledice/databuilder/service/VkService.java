package com.doubledice.databuilder.service;

import com.doubledice.databuilder.bean.BeanBuilder;
import com.doubledice.databuilder.model.Analytic;
import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.User;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
//@Autowired @Lazy
//    VkApiClient vkApiClient;
//    @Autowired @Lazy
//    UserActor userActor;
//    @Autowired @Lazy
//    GroupActor groupActor;
//@Autowired @Lazy
//ServiceActor serviceActor;

    /**
     * @param vkLink - ссылка на сообщество вк
     * @param group  объект группы, который мы создали ранее
     * @return список User с основными данными, взятыми из АПИ (имя, фамилия, вк id, group которую мы обрабатываем)
     * @throws ClientException
     * @throws ApiException
     */
    //todo wrap into tread pull executor
    @Transactional
    public Set<User> getUsersByGroupLink(String vkLink, Group group) throws ClientException, ApiException {
        Integer id = beanBuilder.getVk().utils().resolveScreenName(beanBuilder.getServiceActor(), vkLink).execute().getObjectId();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<User> groupUsers = new HashSet<>();
        int groupSize = beanBuilder.getVk().groups().getMembers(beanBuilder.getServiceActor()).groupId(id.toString()).execute().getCount();
        for (int i = 0; i < groupSize; i += 1000) {
//            List<User> currentUsers = userService.findAll();
            List<Integer> userIds = beanBuilder.getVk().groups().getMembers(beanBuilder.getServiceActor()).groupId(id.toString()).offset(i).execute().getItems();
            beanBuilder.getVk().users().get(beanBuilder.getServiceActor()).userIds(String.valueOf(userIds)).execute().stream().forEach(s -> {
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
        Integer id = beanBuilder.getVk().utils().resolveScreenName(beanBuilder.getServiceActor(), vkLink).execute().getObjectId();
        return beanBuilder.getVk().groups().getByIdObjectLegacy(beanBuilder.getServiceActor()).groupId(id.toString()).execute().get(0).getName();
    }

    @Transactional
    public void scanExistingGroup(Group group, String vkLink) throws ClientException, ApiException {
        ArrayList<User> dataGroupUserList = (ArrayList<User>) group.getUsers();
        Set<User> currentGroupUserList = getUsersByGroupLink(vkLink, group);
        Set<User> exitUsers = new HashSet<>(CollectionUtils.removeAll(dataGroupUserList, currentGroupUserList));
        Set<User> joinedUsers = new HashSet<>(CollectionUtils.removeAll(currentGroupUserList, dataGroupUserList));
        if (!CollectionUtils.isEmpty(exitUsers)) {
            Analytic analytic = new Analytic(group, exitUsers, false);
            analyticService.addAnalytic(analytic);
        }
        if (!CollectionUtils.isEmpty(joinedUsers)) {
            Analytic analytic = new Analytic(group, joinedUsers, true);
            analyticService.addAnalytic(analytic);
        }
        group.setUsers(currentGroupUserList);
        groupService.addGroup(group);
    }
}
