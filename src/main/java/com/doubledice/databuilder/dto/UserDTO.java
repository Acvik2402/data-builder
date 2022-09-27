package com.doubledice.databuilder.dto;

import com.doubledice.databuilder.model.Group;
import com.doubledice.databuilder.model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author ponomarev 30.06.2022
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    @Size(min = 2, message = "user name should have at least 2 characters")
    private String firstName;
    @Size(min = 2, message = "user name should have at least 2 characters")
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lastContact;
    private Boolean hasNotVisitYet;
    private Boolean beenHereOneTime;
    private String chatName;
    private Boolean isInTheChat;
    private String interestGames;
    private String whoInviteHim;
    private Long gamesCount;
    private Long averageOfVisits;
    private List<Subscription> subscriptions;
    private Long gameAccount;
    private String additionalInformation;
    private String vkLink;
    private String tgLink;
    private Boolean wasContacted;
    private List<Group> groups;
}
