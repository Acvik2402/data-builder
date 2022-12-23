package com.doubledice.databuilder.model;

import com.doubledice.databuilder.dto.AnalyticDTO;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * @author ponomarev 17.07.2022
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "analytic")
public class Analytic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //todo add creator Id for sorting in future
    @OneToOne(targetEntity = Group.class)
    private Group group;
    @ManyToMany(targetEntity = User.class,fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    private Set<User> exitUsers;
    @ManyToMany(targetEntity = User.class,fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @ToString.Exclude
    private Set<User> joinedUsers;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @CreatedDate
    private Date date=new Date();

    public Analytic(Group group, Set<User> exitUsers, Set<User> joinedUsers) {
        this.group = group;
        this.exitUsers = exitUsers;
        this.joinedUsers = joinedUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analytic analytic = (Analytic) o;
        return Objects.equals(id, analytic.id) && Objects.equals(group, analytic.group) && Objects.equals(exitUsers, analytic.exitUsers) && Objects.equals(joinedUsers, analytic.joinedUsers) && Objects.equals(date, analytic.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, group, exitUsers, joinedUsers, date);
    }

    @Override
    public String toString() {
        return "Analytic{" +
                "id=" + id +
                ", group=" + group +
                ", exitUsers=" + exitUsers +
                ", joinedUsers=" + joinedUsers +
                ", date=" + date +
                '}';
    }

    //todo configure DTO conversion correctly
    public AnalyticDTO toDto() {
        AnalyticDTO analyticDTO = new AnalyticDTO();
        analyticDTO.setId(getId());
        analyticDTO.setGroup(getGroup());
        analyticDTO.setExitUsers(getExitUsers() != null ? getExitUsers() : null);
        analyticDTO.setJoinedUsers(getJoinedUsers() != null ? getJoinedUsers() : null);
        analyticDTO.setDate(getDate());
        return analyticDTO;
    }
}
