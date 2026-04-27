package com.kritz.restfulapi.model.enums;

public enum TipePembayaran{
    TUNAI("Tunai"),
    QRIS("QRIS");
    
    private final String tipePembayaran;

    TipePembayaran(String tipePembayaran) {
        this.tipePembayaran = tipePembayaran;
    }

    public String toString() {
        return tipePembayaran;
    }

    public static TipePembayaran fromString(String tipePembayaran) {
        for (TipePembayaran s : TipePembayaran.values()) {
            if (s.tipePembayaran.equalsIgnoreCase(tipePembayaran)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Tipe Pembayaran Tidak Diketahui: " + tipePembayaran);
    }
}