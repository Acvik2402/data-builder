package com.doubledice.databuilder.controller.admin;

import com.doubledice.databuilder.model.User;
import com.doubledice.databuilder.service.UserService;
import com.doubledice.databuilder.service.VkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ponomarev 30.06.2022
 */
@Controller
@AllArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private UserService userService;
    VkService vkService;

    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/get/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.findUser(id);
    }

    @GetMapping("/find/{vkLink}")
    public List<User> findUsersByVkGroupLink(@NotNull @PathVariable("vkLink") String vkLink) {
//        return userService.findAllByGroupLink(vkLink).stream().map(user -> objectMapper.convertValue(user, UserDTO.class)).collect(Collectors.toList());
        return userService.findAllByGroupLink(vkLink);
    }

    @GetMapping("/user-list")
    public String findAll(Model model) {
//        List<UserDTO> users = userService.findAll().stream().map(user -> objectMapper.convertValue(user, UserDTO.class)).collect(Collectors.toList());
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @GetMapping("user-delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users/user-list";
    }
}
