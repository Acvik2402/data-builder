package com.doubledice.databuilder.repository;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ponomarev 05.07.2022
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    User findUserByVkLink(String link);
    Optional<User> findUserByVkLink(String link);

    List<User> findAllByGroups(Group group);
}
