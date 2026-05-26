package com.moodbites.restfulapi.model.enums;

public enum Mood{
    SAD("Sad"),
    ANGRY("Angry"),
    HAPPY("Happy"),
    NEUTRAL("Neutral");

    private final String mood;

    Mood(String mood) {
        this.mood = mood;
    }

    public String toString() {
        return mood;
    }

    public static boolean checkExist(String mood){
        for (Mood s : Mood.values()) {
            if (s.mood.equalsIgnoreCase(mood)) {
                return true;
            }
        }
        return false;
    }

    public static Mood fromString(String mood) {
        for (Mood s : Mood.values()) {
            if (s.mood.equalsIgnoreCase(mood)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Mood Unknown: " + mood);
    }
}