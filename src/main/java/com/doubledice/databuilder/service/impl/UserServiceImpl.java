package com.doubledice.databuilder.service.impl;

import com.doubledice.databuilder.model.User;
import com.doubledice.databuilder.repository.GroupRepository;
import com.doubledice.databuilder.repository.UserRepository;
import com.doubledice.databuilder.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author ponomarev 30.06.2022
 */
@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Override
    @Transactional
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllByGroupLink(String vkLink) {
        return userRepository.findAllByGroups(groupRepository.findGroupByVkLink(vkLink).orElse(null));
    }

    @Override
    public User findByVkLink(String link) {
        return userRepository.findUserByVkLink(link).orElse(null);
    }

    @Override
    public User findOrCreateUserByVkLink(String link) {
        return userRepository.findUserByVkLink(link).orElse(new User());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
