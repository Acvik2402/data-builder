package com.doubledice.databuilder.service;

import com.doubledice.databuilder.config.BeansVKConfig;
import com.doubledice.databuilder.dto.AnalyticDTO;
import com.doubledice.databuilder.dto.notification.AnalyticNotification;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ponomarev 16.08.2022
 */
@Service
@RequiredArgsConstructor
public class VkService {
    public static final int ITERATION_SIZE = 100;
    private final UserService userService;
    private final AnalyticService analyticService;
    private final KafkaTemplate<Long, AnalyticNotification> kafkaTemplate;
    private final GroupService groupService;
    @Lazy
    private VkApiClient vkApiClient;
    @Lazy
    private ServiceActor serviceActor;

  @Autowired
  public VkService(UserService userService, AnalyticService analyticService,
                   KafkaTemplate<Long, AnalyticNotification> kafkaTemplate,
                   GroupService groupService,
                   VkApiClient vkApiClient,
                   ServiceActor serviceActor) {
    this.userService = userService;
    this.analyticService = analyticService;
    this.kafkaTemplate = kafkaTemplate;
    this.groupService = groupService;
    this.vkApiClient = vkApiClient;
    this.serviceActor = serviceActor;
  }

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
    public Set<User> getUsersByGroup(Group group) throws ClientException, ApiException {
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
                List<String> userIds = vkApiClient.groups().getMembers(serviceActor).groupId(id.toString())
                        .count(ITERATION_SIZE).offset(i).execute().getItems().stream().map(String::valueOf).collect(Collectors.toList());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                groupUsers.addAll(getUsersByVkId(userIds, group));
            }
            return groupUsers;
        }
    }

    public void scanExistingGroup(Group group) throws ClientException, ApiException {
        List<String> dataGroupUserIdList = new ArrayList<>(group.getVkUsersIdLinks());
        List<String> currentGroupUserIdList = new ArrayList<>(getUsersIdLinksByGroupLink(group.getVkLink()));
        List<String> exitUsersId = new ArrayList<>(dataGroupUserIdList);
        exitUsersId.removeAll(currentGroupUserIdList);
        List<String> joinedUsersId = new ArrayList<>(currentGroupUserIdList);
        joinedUsersId.removeAll(dataGroupUserIdList);
        synchronized (this) {
            if (!CollectionUtils.isEmpty(exitUsersId) || !CollectionUtils.isEmpty(joinedUsersId)) {
                Set<User> exitUsers = getUsersByVkId(exitUsersId, null);
                Set<User> joinedUsers = getUsersByVkId(joinedUsersId, group);
                //todo add creator Id for sorting in future    
                Analytic analytic = new Analytic(group, exitUsers, joinedUsers);
                AnalyticNotification analyticNotification = convertData(analyticService.addAnalytic(analytic));
                kafkaTemplate.send("analytic.new", analyticNotification);
                if (!CollectionUtils.isEmpty(exitUsersId)) {
                    updateUsersInfo(exitUsers, group, true);
                }
            }
        }
    }

  private AnalyticNotification convertData(Analytic analytic) {
    AnalyticNotification analyticNotification = new AnalyticNotification("Изменение в участниках группы",
            analytic.getGroup().getGroupName(),
            analytic.toString());
    return analyticNotification;
  }

  /**
     * обновляем информацию в БД об участниках группы, если true - то удаляем
     *
     * @param users  участники, которые пришли или ушли
     * @param group  группа ВК
     * @param remove удалять или добавлять участников, если true - то удаляем
     */
    private synchronized void updateUsersInfo(Set<User> users, Group group, boolean remove) {
        if (remove) {
            users.forEach(s -> {
                s.removeGroup(group);
                userService.addUser(s);
            });
        }
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
                groupUsersLinks.addAll(vkApiClient.groups().getMembers(serviceActor).groupId(id.toString())
                        .count(ITERATION_SIZE).offset(i).execute().getItems().stream().map(String::valueOf).collect(Collectors.toList()));
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

    public static String checkVkLink(String vkLink) {
        vkLink=vkLink.trim();
        String pattern = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
        Pattern regex = Pattern.compile("(?<=\\.\\D{3}\\/)(\\w+)\\/?");
        Matcher matcher =  regex.matcher(vkLink);
        if (Pattern.matches(pattern, vkLink)&&matcher.find()) {
            return matcher.group(0);
        }
        return vkLink;
    }

    public String getVkId(String vkLink) {
        Integer id = null;
        try {
            id = vkApiClient.utils().resolveScreenName(serviceActor, vkLink).execute().getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }
}
