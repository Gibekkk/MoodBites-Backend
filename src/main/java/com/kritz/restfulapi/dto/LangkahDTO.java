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
public class LangkahDTO {
    private String deskripsi;
    private int urutan;

    public void checkDTO() {
        trim();
        if(this.deskripsi == null) throw new IllegalArgumentException("Deskripsi Tidak Boleh Bernilai NULL");
        if(this.urutan <= 0) throw new IllegalArgumentException("Urutan Langkah Tidak Boleh Kurang Dari Sama Dengan 0");
    }

    public void trim() {
        this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
