package com.example.oron.androidfinalproject.Model;

public class Trip {
    private String name;
    private String id;
    private String type;
    private int age_min;
    private int age_max;
    private int difficulty;
//    private Boolean isChecked = false;
//    private int year;
//    private int monthOfYear;
//    private int dayOfMonth;
//    private int hourOfDay;
//    private int minute;

    public Trip(String name, String id, String type, int age_min, int age_max, int difficulty) {// int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.age_min = age_min;
        this.age_max = age_max;
        this.difficulty = difficulty;
//        this.isChecked = isChecked;
//        this.year = year;
//        this.monthOfYear = monthOfYear;
//        this.dayOfMonth = dayOfMonth;
//        this.hourOfDay = hourOfDay;
//        this.minute = minute;
    }

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

    public int getAge_max() {
        return age_max;
    }

    public void setAge_max(int age_max) {
        this.age_max = age_max;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
