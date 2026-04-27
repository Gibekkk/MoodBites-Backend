package com.kritz.restfulapi.model.enums;

public enum Level{
    TOKO("Toko");
    
    private final String level;

    Level(String level) {
        this.level = level;
    }

    public String toString() {
        return level;
    }

    public static Level fromString(String level) {
        for (Level s : Level.values()) {
            if (s.level.equalsIgnoreCase(level)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Level Tidak Diketahui: " + level);
    }
}