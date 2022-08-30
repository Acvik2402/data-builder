package com.doubledice.databuilder.repository;

import com.doubledice.databuilder.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ponomarev 16.07.2022
 */
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findGroupByVkLink(String VkLink);
}
