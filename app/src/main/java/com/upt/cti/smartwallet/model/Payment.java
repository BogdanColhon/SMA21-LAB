package com.upt.cti.smartwallet.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Payment  implements Serializable {

    public String timestamp;
    private double cost;
    private String name;
    private String type;
    private String user;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Payment(double cost, String name, String type,String user) {
        this.cost=cost;
        this.name=name;
        this.type=type;
        this.user=user;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "timestamp='" + timestamp + '\'' + "cost=" + cost + "name='" + name + '\'' + "type='" + type;
    }
    public Payment makeCopy(){
        Payment copyPayment = new Payment(this.cost, this.name, this.type,this.user);
        copyPayment.timestamp = this.timestamp;
        return copyPayment;
    }
}