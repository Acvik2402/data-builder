package com.doubledice.databuilder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author ponomarev 30.06.2022
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vk_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
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
    @OneToMany
    @Column(name = "subscription", nullable = false)
    @ToString.Exclude
    private Set<Subscription> subscriptions;
    private Long gameAccount;
    private String additionalInformation;
    @Column(name = "vk_link", unique = true)
    private String vkLink;
    private String tgLink;
    private Boolean wasContacted;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    private Set<Group> groups;

    public void setNewGroupIfNotExist(Group group) {
        if (group != null) {
            if (CollectionUtils.isEmpty(this.groups)) {
                HashSet<Group> tempSet = new HashSet<>();
                tempSet.add(group);
                this.groups = tempSet;
            } else {
                this.groups.add(group);
            }
        }
    }

    public void removeGroup(Group group) {
        if (group != null && !CollectionUtils.isEmpty(this.groups)) {
            this.groups.remove(group);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id) && Objects.equals(vkLink, user.vkLink);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", lastContact=" + lastContact +
                ", hasNotVisitYet=" + hasNotVisitYet +
                ", beenHereOneTime=" + beenHereOneTime +
                ", chatName='" + chatName + '\'' +
                ", isInTheChat=" + isInTheChat +
                ", interestGames='" + interestGames + '\'' +
                ", whoInviteHim='" + whoInviteHim + '\'' +
                ", gamesCount=" + gamesCount +
                ", averageOfVisits=" + averageOfVisits +
//                ", subscriptions=" + subscriptions +
                ", gameAccount=" + gameAccount +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", vkLink='" + vkLink + '\'' +
                ", tgLink='" + tgLink + '\'' +
                ", wasContacted=" + wasContacted +
                ", groups=" + groups +
                '}';
    }
}
