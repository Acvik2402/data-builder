package com.doubledice.databuilder.controller;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.service.AnalyticService;
import com.doubledice.databuilder.service.GroupService;
import com.doubledice.databuilder.service.VkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vk.api.sdk.exceptions.ApiAccessException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ponomarev 16.07.2022
 */
@Controller()
@AllArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private GroupService groupService;
    private AnalyticService analyticService;
    private VkService vkService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/groups")
    public String findAll(Model model) {
//        List<GroupDTO> groups = groupService.findAll().stream().map(group -> objectMapper.convertValue(group, GroupDTO.class)).collect(Collectors.toList());
        List<Group> groups = groupService.findAll();
        model.addAttribute("groups", groups);
        return "group-list";
    }

    @GetMapping("/group-create")
    public String createGroupForm(Group group, Model model) {
        model.addAttribute("group", new Group());
        return "group-create";
    }

    @PostMapping("/group-create")
    public String addGroup(@Valid Group group, BindingResult result, Model model) {
        model.addAttribute("group", new Group());
        if (result.hasErrors()) {
            return "group-create";
        }
        groupService.addGroup(group);
        return "redirect:/group/groups";
    }

    @GetMapping("group-delete/{id}")
    public String deleteGroup(@PathVariable("id") Long id) {
        groupService.deleteById(id);
        return "redirect:/group/groups";
    }

    //todo fix list columns
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Group group = groupService.findGroup(id);
//        GroupDTO groupDTO = objectMapper.convertValue(groupService.findGroup(id), Group.class);
        model.addAttribute("group", group);
        return "group-update";
    }

    @PostMapping("/group-update/{id}")
    public String updateGroup(@PathVariable("id") Long id, @Valid Group group,
                              BindingResult result, Model model) {
//        model.addAttribute("group", new GroupDTO());
        if (result.hasErrors()) {
            group.setId(id);
            return "group-update";
        }
        groupService.addGroup(group);
//        groupService.addGroup(objectMapper.convertValue(group, Group.class));
        return "redirect:/group/groups";
    }

    /**
     * @param vkLink link of some group from vk.com (like URL link or just ID)
     * @return if croup table exist:
     * get in VK API user List
     * create new List and compare with existing list from BD
     * and save result in Analytic table
     * else: just create new table with users from this group
     */
    @GetMapping(path = "/scan-group/")
    public String scanGroup(@RequestParam String vkLink) {
        vkLink = checkVkLink(vkLink);
        Group group = groupService.findGroupByLink(vkLink);
        try {
            if (group != null) {
                Group finalGroup = group;
                CompletableFuture.runAsync(() -> {
                    try {
                        vkService.scanExistingGroup(finalGroup);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                group = new Group(vkLink, new HashSet<>());
                group.setGroupName(vkService.getGroupNameByVKLink(vkLink));
                group.setVkLink(vkLink);
                groupService.addGroup(group);
                Group finalGroup = group;
                CompletableFuture.runAsync(() -> {
                    try {
                        addGroupUsers(finalGroup);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return "redirect:/group/groups";
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            return "redirect:/group/groups";
        }

        //    @PostMapping("/add")
//    public ResponseEntity<GroupDTO> addNewGroup(@Valid @RequestBody GroupDTO groupDTO) {
//        return new ResponseEntity<GroupDTO>(objectMapper.convertValue(groupService.addGroup(objectMapper.convertValue(groupDTO, Group.class)), GroupDTO.class), HttpStatus.CREATED);
//    }
    }

    private void addGroupUsers(Group group) throws ClientException, ApiException {
        try {
           group.setVkUsers(vkService.getUsersByGroup(group));
        } catch (ApiAccessException e) {
            group.setAdditionalInformation(e.getMessage());
        }finally {
            groupService.addGroup(group);
        }
    }

    private String checkVkLink(String vkLink) {
        vkLink=vkLink.trim();
        String pattern = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
        Pattern regex = Pattern.compile("(?<=\\.\\D{3}\\/)(\\w+)\\/?");
        Matcher matcher =  regex.matcher(vkLink);
        if (Pattern.matches(pattern, vkLink)&&matcher.find()) {
            return matcher.group(0);
        }
        return vkLink;
    }
}
