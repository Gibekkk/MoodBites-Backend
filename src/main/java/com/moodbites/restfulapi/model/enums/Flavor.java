package com.moodbites.restfulapi.model.enums;

public enum Flavor{
    SWEET("Manis"),
    SPICY("Pedas"),
    SALTY("Asin / Gurih"),
    BITTER("Pahit"),
    SOUR("Asam / Segar");

    private final String flavor;

    Flavor(String flavor) {
        this.flavor = flavor;
    }

    public String toString() {
        return flavor;
    }

    public static boolean checkExist(String flavor){
        for (Flavor s : Flavor.values()) {
            if (s.flavor.equalsIgnoreCase(flavor)) {
                return true;
            }
        }
        return false;
    }

    public static Flavor fromString(String flavor) {
        for (Flavor s : Flavor.values()) {
            if (s.flavor.equalsIgnoreCase(flavor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Flavor Unknown: " + flavor);
    }
}