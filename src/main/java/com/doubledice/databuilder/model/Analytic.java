package com.doubledice.databuilder.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author ponomarev 17.07.2022
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "analytic")
public class Analytic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(targetEntity = Group.class)
    private Group group;
    @OneToMany(targetEntity = User.class,fetch = FetchType.EAGER, mappedBy = "id")
    private Set<User> exitUsers;
    @OneToMany(targetEntity = User.class,fetch = FetchType.EAGER, mappedBy = "id")
    private Set<User> joinedUsers;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private Date date = new Date();

    public Analytic(Group group, Set<User> exitUsers, Set<User> joinedUsers, boolean inOrOut) {
        this.group = group;
        this.exitUsers = exitUsers;
        this.joinedUsers = joinedUsers;
    }
}
