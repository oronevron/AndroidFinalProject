package com.example.oron.androidfinalproject.Model;

import java.util.LinkedList;
import java.util.List;

public class Model {
    private static Model instance = new Model();
    private List<Trip> tripsData = new LinkedList<Trip>();

    private Model() {

        // Add initial trips data
        for (int i = 0; i < 10; i++){
            Trip trip = new Trip("id" + i, "trip" + i, "type" + i, i, i + 5, i + 1);
            addTrip(trip);
        }
    }

    public static Model getInstance() {
        return instance;
    }

    public List<Trip> getAllTrips() {
        return tripsData;
    }

    public void addTrip(Trip trip){
        tripsData.add(trip);
    }

    public void deleteTrip(int index){
        tripsData.remove(index);
    }

    public void editTrip(Trip trip, int index){
        tripsData.set(index, trip);
    }

    public Trip getTripByIndex(int index) {
        return tripsData.get(index);
    }
}
