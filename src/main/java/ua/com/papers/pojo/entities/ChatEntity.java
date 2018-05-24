package ua.com.papers.pojo.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "chat")
public class ChatEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="initiator_user_id", nullable = false)
    private UserEntity initiatorUser;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_to_chat", joinColumns = { @JoinColumn(name = "chat_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<UserEntity> members;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getInitiatorUser() {
        return initiatorUser;
    }

    public void setInitiatorUser(UserEntity initiatorUser) {
        this.initiatorUser = initiatorUser;
    }

    public Set<UserEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<UserEntity> members) {
        this.members = members;
    }
}
