package com.example.oron.androidfinalproject.Model;

public class Trip {
    private String name;
    private String id;
    private String type;
    private int age_min;
    private int difficulty;
    private String image_name;
//    private Boolean isChecked = false;
//    private int year;
//    private int monthOfYear;
//    private int dayOfMonth;
//    private int hourOfDay;
//    private int minute;

    public Trip(String name, String id, String type, int age_min, int difficulty, String image_name) {// int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.age_min = age_min;
        this.difficulty = difficulty;
        this.image_name = image_name;
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

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getImageName() {
        return image_name;
    }

    public void setImageName(String image_name) {
        this.image_name = image_name;
    }
}
