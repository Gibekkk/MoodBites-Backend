package com.kritz.restfulapi.model.enums;

public enum Kategori{
    MINUMAN("Minuman"),
    MAKANAN_RINGAN("Makanan Ringan"),
    MAKANAN_BERAT("Makanan Berat");
    
    private final String kategori;

    Kategori(String kategori) {
        this.kategori = kategori;
    }

    public String toString() {
        return kategori;
    }

    public static Kategori fromString(String kategori) {
        for (Kategori s : Kategori.values()) {
            if (s.kategori.equalsIgnoreCase(kategori)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Kategori Tidak Diketahui: " + kategori);
    }
}