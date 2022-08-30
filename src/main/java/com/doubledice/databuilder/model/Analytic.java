package com.doubledice.databuilder.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
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
    @ManyToMany(targetEntity = User.class)
    private Set<User> users;

    @CreatedDate
    private Date date = new Date();

    private boolean inOrOut; //if true - user is  entered
    public Analytic(Group group, Set<User> users, boolean inOrOut) {
        this.group = group;
        this.users = users;
        this.inOrOut = inOrOut;
    }
}
