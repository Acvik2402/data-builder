package com.doubledice.databuilder.controller.admin;

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

import static com.doubledice.databuilder.service.VkService.checkVkLink;

/**
 * @author ponomarev 16.07.2022
 */
@Controller()
@AllArgsConstructor
@RequestMapping("/admin/group")
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
    @GetMapping("group-delete/{id}")
    public String deleteGroup(@PathVariable("id") Long id) {
        groupService.deleteById(id);
        return "redirect:/admin/group/groups";
    }
}
