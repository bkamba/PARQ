package com.example.leo.parq.Users;
import java.util.*;

/**
 * Created by cmaso on 4/8/2017.
 */

public class Parqr extends User {
    private TreeMap<Long, Vehicle> vehicles; //Collection of registered vehicles
    private ArrayList payment; //Collection of payment options

//  Constructor: Calls User class constructor, initializes vehicle and payment collections
    public Parqr(String first_name, String last_name, String email, String phone,
                 String username, String password) {
        super(first_name, last_name, email, phone, username, password);
        vehicles = new TreeMap(new ParqComparator());
        payment = new ArrayList();
    }

//  Adds new card to collection of payments
    public void addPayment(String card) {
        payment.add(card);
    }

//  Registers new vehicle under Parqr's account
    public void addVehicle(String make, String model, String year, String liscense) {
        Vehicle new_vehicle = new Vehicle(make, model, year, liscense);
        vehicles.put(new_vehicle.getID(), new_vehicle);
    }

// Returns Parqr's entire collection of registered vehicles
    public TreeMap getAllVehicles() {
        return vehicles;
    }

//  Returns specific vehicle from Parqr's collection of registered vehicles
    public Vehicle getVehicle(long target) {
        return vehicles.get(target);
    }
}



