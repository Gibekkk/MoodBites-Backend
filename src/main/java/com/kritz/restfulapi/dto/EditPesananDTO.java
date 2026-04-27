package com.kritz.restfulapi.dto;

import lombok.Setter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditPesananDTO {
    private String deskripsi;
    private int jumlah;


    public void checkDTO() {
        trim();
        if(this.jumlah <= 0) throw new IllegalArgumentException("Jumlah Pesanan Tidak Valid");
    }

    public void trim() {
        this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}

