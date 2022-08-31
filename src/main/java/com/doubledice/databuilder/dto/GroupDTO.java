package com.doubledice.databuilder.dto;

import com.doubledice.databuilder.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author ponomarev 16.07.2022
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    private long id;
    private String groupName;
    private String vkLink;
    private Set<User> users;
    private String additionalInformation;
}
