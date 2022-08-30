package com.doubledice.databuilder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<Long> users;
    private String additionalInformation;
}
