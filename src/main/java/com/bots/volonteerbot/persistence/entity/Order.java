package com.bots.volonteerbot.persistence.entity;

import com.bots.volonteerbot.persistence.type.OrderState;

import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private BotUser botUser;

    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private String address;

    @Column(columnDefinition = "TEXT")
    private String medicines;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String food;

    @Column(name = "other_info", columnDefinition = "TEXT")
    private String otherInfo;

    @Column(name = "order_state")
    @Enumerated(value = EnumType.STRING)
    private OrderState orderState;

    public BotUser getBotUser() {
        return botUser;
    }

    public void setBotUser(BotUser botUser) {
        this.botUser = botUser;
    }

    public String getMedicines() {
        return medicines;
    }

    public void setMedicines(String medicines) {
        this.medicines = medicines;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    @Override
    public String toString() {
        return name + ", вік: " + getAge() + ", " + phoneNumber + ", " + address + ", <b>не можу сам(а), тому що:</b> " + reason + ", <b>їжа:</b> " + food + ", <b>ліки:</b> " + medicines + ", <b>примітки:</b> " + otherInfo;
    }
}
