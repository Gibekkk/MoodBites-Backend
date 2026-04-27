package com.kritz.restfulapi.model.enums;

public enum OTPType{
    REGISTER("Register"),
    CHANGE_EMAIL("Change Email");
    
    private final String otpType;

    OTPType(String otpType) {
        this.otpType = otpType;
    }

    public String toString() {
        return otpType;
    }

    public static OTPType fromString(String otpType) {
        for (OTPType s : OTPType.values()) {
            if (s.otpType.equalsIgnoreCase(otpType)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Tipe OTP Tidak Diketahui: " + otpType);
    }
}