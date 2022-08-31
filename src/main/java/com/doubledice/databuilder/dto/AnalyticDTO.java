package com.doubledice.databuilder.dto;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

/**
 * @author ponomarev 17.07.2022
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticDTO {
    private long id;
    private Group group;
    private Set<User> exitUsers;
    private Set<User> joinedUsers;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private Date date;
}
