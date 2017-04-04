package com.example.oron.androidfinalproject.Model;

import android.graphics.Bitmap;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Trip {
    private String name;
    private String id;
    private String user_id;
    private String type;
    private int age_min;
    private int difficulty;
    private String imageName;
    private Bitmap image;
    private double lastUpdated;
    private boolean isDeleted;

    public Trip(String name, String type, int age_min, int difficulty) {// int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        this.name = name;
        this.type = type;
        this.age_min = age_min;
        this.difficulty = difficulty;
    }

    public Trip() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge_min() {
        return age_min;
    }

    public void setAge_min(int age_min) {
        this.age_min = age_min;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setImage(Bitmap image) {this.image = image; }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
//        result.put("id", id);
        result.put("name", name);
        result.put("imageName", imageName);
        result.put("type", type);
        result.put("difficulty", difficulty);
        result.put("age_min", age_min);
        result.put("lastUpdated", ServerValue.TIMESTAMP);
        result.put("user_id", user_id);
        result.put("isDeleted", isDeleted);
        return result;
    }
}
