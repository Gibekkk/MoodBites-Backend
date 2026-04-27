package com.kritz.restfulapi.model.enums;

public enum StatusPenjualan{
    KERANJANG("Dalam Keranjang"),
    TERJUAL("Sudah Terjual"),
    PEMBAYARAN("Dalam Pembayaran");
    
    private final String statusPenjualan;

    StatusPenjualan(String statusPenjualan) {
        this.statusPenjualan = statusPenjualan;
    }

    public String toString() {
        return statusPenjualan;
    }

    public static StatusPenjualan fromString(String statusPenjualan) {
        for (StatusPenjualan s : StatusPenjualan.values()) {
            if (s.statusPenjualan.equalsIgnoreCase(statusPenjualan)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status Penjualan Tidak Diketahui: " + statusPenjualan);
    }
}