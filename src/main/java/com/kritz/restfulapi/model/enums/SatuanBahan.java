package com.kritz.restfulapi.model.enums;

public enum SatuanBahan{
    LITER("Liter"),
    MILILITER("Mililiter"),
    GRAM("Gram"),
    MILIGRAM("Miligram"),
    PAX("Pax");
    
    private final String satuanBahan;

    SatuanBahan(String satuanBahan) {
        this.satuanBahan = satuanBahan;
    }

    public String toString() {
        return satuanBahan;
    }

    public static SatuanBahan fromString(String satuanBahan) {
        for (SatuanBahan s : SatuanBahan.values()) {
            if (s.satuanBahan.equalsIgnoreCase(satuanBahan)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Satuan Bahan Tidak Diketahui: " + satuanBahan);
    }
}