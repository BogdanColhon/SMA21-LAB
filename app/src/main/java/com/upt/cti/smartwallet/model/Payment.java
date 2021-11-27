package com.upt.cti.smartwallet.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Payment {

    public String timestamp;
    private double cost;
    private String name;
    private String type;

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }
    public Payment(double cost, String name, String type) {
        this.cost=cost;
        this.name=name;
        this.type=type;
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
}