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
public class ProfilDTO {
    private String nama;
    private String deskripsi;


    public void checkDTO() {
        trim();
        if(this.nama == null) throw new IllegalArgumentException("Nama Toko Tidak Boleh Bernilai NULL");
    }

    public void trim() {
        this.nama = Optional.ofNullable(this.nama).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}

