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
     * @param vkLink link of some group from vk.com
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
                vkService.scanExistingGroup(group, vkLink);
            } else {
                group = new Group(vkLink, new HashSet<>());
                group.setGroupName(vkService.getGroupNameByVKLink(vkLink));
                group.setVkLink(vkLink);
                //todo saving into DB 3 times, should fix it
                group = groupService.addGroup(group);
                try {
                    group.setUsers(vkService.getUsersByGroupLink(vkLink, group));
                } catch (ApiAccessException e) {
                    group.setAdditionalInformation(e.getMessage());
                }
                groupService.addGroup(group);
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

    private String checkVkLink(String vkLink) {
        //todo add regex cropping full vk link.

        return vkLink;
    }
}
