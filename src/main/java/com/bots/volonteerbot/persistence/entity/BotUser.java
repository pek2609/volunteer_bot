package com.bots.volonteerbot.persistence.entity;

import com.bots.volonteerbot.persistence.type.RoleType;
import com.bots.volonteerbot.persistence.type.State;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "users")
public class BotUser extends BaseEntity {

    @Column(name = "chat_id", unique = true)
    @NotNull
    private Long chatId;

    @Column(name = "username", unique = true)
    private String userName;

    @Enumerated(value = EnumType.STRING)
    private RoleType role;

    @Enumerated(value = EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "botUser", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<Order> orders;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
