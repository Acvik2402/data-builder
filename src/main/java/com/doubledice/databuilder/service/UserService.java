package com.doubledice.databuilder.service;

import com.doubledice.databuilder.dto.UserDTO;
import com.doubledice.databuilder.model.User;

import java.util.List;

/**
 * @author ponomarev 30.06.2022
 */
public interface UserService {
    User addUser(User user);

    User findUser(Long id);

    List<User> findAll();

    List<User> findAllByGroupLink(String vkLink);

    User findByVkLink(String link);

    User findOrCreateUserByVkLink(String link);

    void deleteById(Long id);

}
