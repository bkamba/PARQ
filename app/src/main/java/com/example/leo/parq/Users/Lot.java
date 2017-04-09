package com.example.leo.parq.Users;

import java.util.*;
import java.text.*;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cmaso on 4/8/2017.
 */

//  Lot object associated with Providers.

public class Lot {
    private ArrayList images;
    private String description;
    private boolean inst_verify, online;
    private int start_hr, start_min, end_hr, end_min;
    private double rate;
    private LatLng location;
    private long ID;
    private Random random_gen = new Random();

    public Lot(double rate, int start_hr, int start_min, int end_hr, int end_min, LatLng location) {
        this.rate = rate;
        this.start_hr = start_hr;
        this.start_min = start_min;
        this.end_hr = end_hr;
        this.end_min = end_min;
        this.location = location;
        this.ID = random_gen.nextLong();
        images = new ArrayList();
    }

    public long getID() {
        return ID;
    }

    public double getRate() {
        return rate;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVerify(boolean inst_verify) {
        this.inst_verify = inst_verify;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void goOnline(boolean force) {
        Calendar cal = Calendar.getInstance();

        if (force) {
            online = true;
        } else {
            if (cal.HOUR > start_hr && cal.HOUR < end_hr) {
                online = true;

            } else if (cal.HOUR == start_hr && cal.MINUTE > start_min) {
                online = true;

            } else if (cal.HOUR == end_hr && cal.MINUTE < end_min) {
                online = true;

            } else {
                online = false;

            }
        }
    }

    public void goOffline() {
        online = false;
    }

    public boolean instantVerify() {
        return inst_verify;
    }

    public void addImage(String img) {
        images.add(img);
    }
}
