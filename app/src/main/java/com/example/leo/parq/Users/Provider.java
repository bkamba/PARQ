package com.example.leo.parq.Users;
import com.google.android.gms.maps.model.LatLng;

import java.util.*;

/**
 * Created by cmaso on 4/8/2017.
 */

public class Provider extends User {
    private TreeMap<Long,Lot> lots;
    private String bank_acct, rules;
    private double rating;


//    Constructor: Calls User class constructor, initalizes bank_acct, rating, and
//    collection of Lots.
    public Provider(String first_name, String last_name, String email, String phone,
                    String username, String password, String bank_acct) {
        super(first_name, last_name, email, phone, username, password);
        this.bank_acct = bank_acct;
        lots = new TreeMap(new ParqComparator());
        rating = 0.0;
    }

//  Creates new Lot obj with given parameters, adds mapping of new ID and Lot to collection
    public void addLot(double rate, int start_hr, int start_min, int end_hr, int end_min,
                       LatLng location) {
        Lot new_lot = new Lot(rate, start_hr, start_min, end_hr, end_min, location, Lot.lotType.DRIVEWAY ,this);
        lots.put(new_lot.getID(), new_lot);
    }

//  Returns entire collection of lots
    public TreeMap getAllLots() {
        return lots;
    }

//  Returns specific Lot obj according to target parameter
    public Lot getLot(long target) {
        return lots.get(target);
    }

// Updates Provider's rating
    public void setRating(double rating) {
        this.rating = rating;
    }

//  Updates Provider's specific rules
    public void setRules(String rules) {
        this.rules = rules;
    }
}
