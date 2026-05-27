package com.moodbites.restfulapi.model.enums;

public enum SampleFood{
    NASI_AYAM("Nasi Ayam (Goreng / Panggang)"),
    NASI_GORENG("Nasi Goreng / Nasi Gila"),
    OLAHAN_MIE("Olahan Mie (Mie Kuah / Goreng / Yamien)"),
    BAKSO_KUAH("Bakso Kuah"),
    CEMILAN_KENTANG("Cemilan Kentang Goreng / Nugget"),
    CEMILAN_GURIH("Cemilan Gurih (Tahu / Jamur Crispy)"),
    CEMILAN_MANIS("Cemilan Manis (Pisang / Ubi / Bakara Goreng)"),
    PUDING_DESSERT("Puding / Dessert Manis"),
    AIR_MINERAL("Air Mineral"),
    ES_TEH("Es Teh / Teh Kemasan"),
    KOPI_KEMASAN("Kopi Kemasan"),
    MINUMAN_SODA("Minuman Soda"),
    MINUMAN_SUSU("Minuman Susu / Coklat"),
    MINUMAN_RASA_BUAH("Minuman Rasa Buah"),
    MINUMAN_VIT_C("Minuman Vitamin C / Asam Segar"),
    MINUMAN_ISOTONIK("Minuman Isotonik");

    private final String sampleFood;

    SampleFood(String sampleFood) {
        this.sampleFood = sampleFood;
    }

    public String toString() {
        return sampleFood;
    }

    public static boolean checkExist(String sampleFood){
        for (SampleFood s : SampleFood.values()) {
            if (s.sampleFood.equalsIgnoreCase(sampleFood)) {
                return true;
            }
        }
        return false;
    }

    public static SampleFood fromString(String sampleFood) {
        for (SampleFood s : SampleFood.values()) {
            if (s.sampleFood.equalsIgnoreCase(sampleFood)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Sample Food Unknown: " + sampleFood);
    }
}