package com.kritz.restfulapi.dto.lists;

import lombok.Setter;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BahanList {
    private String idBahan;
    private double jumlahBahan;

    public void checkDTO() {
        trim();
        if(this.idBahan == null) throw new IllegalArgumentException("ID Bahan Tidak Boleh Bernilai NULL");
        if(this.jumlahBahan <= 0) throw new IllegalArgumentException("Jumlah Bahan Tidak Boleh Kurang Dari Sama Dengan 0");
    }

    public void trim() {
        this.idBahan = Optional.ofNullable(this.idBahan).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
