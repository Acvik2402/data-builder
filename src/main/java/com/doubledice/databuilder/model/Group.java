package com.doubledice.databuilder.model;

import com.vk.api.sdk.objects.annotations.Required;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * @author ponomarev 16.07.2022
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName = null;
    @Column(name = "vk_link", unique = true)
    private String vkLink;
    @Required(value = false)
    @ManyToMany(mappedBy = "groups", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<User> vkUsers;
    @Required(value = false)
    private String additionalInformation;

    public Group(String vkLink, Set<User> users) {
        this.vkLink = vkLink;
        this.vkUsers = users;
    }

    public Group(String vkLink) {
        this.vkLink = vkLink;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", vkLink='" + vkLink + '\'' +
//                ", users=" + users +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Group group = (Group) o;
        return id != null && Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
