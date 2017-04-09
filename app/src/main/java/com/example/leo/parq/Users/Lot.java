package com.example.leo.parq.Users;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.*;
import java.text.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by cmaso on 4/8/2017.
 */

//  Lot object associated with Providers.

public class Lot implements ClusterItem{
    private ArrayList images;
    private String description;
    private boolean inst_verify, online;
    private int start_hr, start_min, end_hr, end_min;
    private double rate;
    private LatLng location;
    private long ID;
    private Random random_gen = new Random();
    private lotType type;
    private Provider owner;
    public enum lotType { GARAGE, DRIVEWAY, STREET, LAWN;
    @Override
        public String toString(){
        switch (this){
            case GARAGE:
                return "Garage";
            case DRIVEWAY:
                return "Driveway";
            case STREET:
                return "Street";
            case LAWN:
                return "Lawn";
        }
        return "Lot";
    }};

    public Lot(double rate, int start_hr, int start_min, int end_hr, int end_min,
               LatLng location, lotType type, Provider user) {
        this.rate = rate;
        this.start_hr = start_hr;
        this.start_min = start_min;
        this.end_hr = end_hr;
        this.end_min = end_min;
        this.location = location;
        this.ID = random_gen.nextLong();
        this.type = type;
        owner = user;
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


    private String calculateRate(int start_hr, int start_min, int end_hr, int end_min){
        int total = start_min + end_min > 60 ? 2 : 1;
        total += end_hr - start_hr;
        return "$"+total*this.rate;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return this.description;
    }
}
