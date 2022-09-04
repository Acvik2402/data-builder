package com.doubledice.databuilder.controller;

import com.doubledice.databuilder.dto.UserDTO;
import com.doubledice.databuilder.model.User;
import com.doubledice.databuilder.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ponomarev 30.06.2022
 */
@Controller
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/user-create")
    public String createUserForm(UserDTO user, Model model) {
        model.addAttribute("user", new UserDTO());
        return "user-create";
    }

    @PostMapping("/user-create")
    public String addUser(@Valid UserDTO user, BindingResult result, Model model) {
        model.addAttribute("user", new UserDTO());
        if (result.hasErrors()) {
            return "user-create";
        }
        userService.addUser(objectMapper.convertValue(user, User.class));
        return "redirect:/users/user-list";
    }

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.findUser(id);
    }

    @GetMapping("/find/{vkLink}")
    public List<User> findUserByVkLink(@NotNull @PathVariable("vkLink") String vkLink) {
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
        return "redirect:/users/user-list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findUser(id);
//        UserDTO userDTO = objectMapper.convertValue(userService.findUser(id), User.class);
        model.addAttribute("user", user);
        return "user-update";
    }

    @PostMapping("/user-update/{id}")
    public String updateUser(@PathVariable("id") Long id, @Valid User user,
                             BindingResult result, Model model) {
//        model.addAttribute("user", new UserDTO());
        if (result.hasErrors()) {
            user.setId(id);
            return "user-update";
        }
        userService.addUser(user);
//        userService.addUser(objectMapper.convertValue(user, User.class));
        return "redirect:/users/user-list";
    }

//    @PostMapping("add")
//    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserDTO userDTO) {
//        return new ResponseEntity<UserDTO>(objectMapper.convertValue(userService.addUser(objectMapper.convertValue(userDTO, User.class)), UserDTO.class), HttpStatus.CREATED);
//    }
//
//    @GetMapping("get/{id}")
//    public User getUser(@PathVariable("id") Long id) {
//        return userService.findUser(id);
//    }
//
//    @GetMapping("find/{vkLink}")
//    public List<UserDTO> findUserByVkLink(@NotNull @PathVariable("vkLink") String vkLink) {
//        return userService.findAllByGroupLink(vkLink).stream().map(user -> objectMapper.convertValue(user, UserDTO.class)).collect(Collectors.toList());
//    }
}
