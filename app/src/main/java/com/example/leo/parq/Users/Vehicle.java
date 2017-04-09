package com.example.leo.parq.Users;

import java.util.*;

/**
 * Created by cmaso on 4/8/2017.
 */

//Vehicle Object associated with Parqrs

public class Vehicle {
    private String make, model, year, liscense;
    private ArrayList images = new ArrayList();
    private long ID;
    private Random random_gen = new Random();

    public Vehicle(String make, String model, String year, String liscense) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.liscense = liscense;
        this.ID = random_gen.nextLong();
    }

    public long getID() {
        return ID;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    public String getLiscense() {
        return liscense;
    }

    public void addImage(String img) {
        images.add(img);
    }
}
