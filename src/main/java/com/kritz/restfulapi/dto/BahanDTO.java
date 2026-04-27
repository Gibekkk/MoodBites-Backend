package com.kritz.restfulapi.dto;

import lombok.Setter;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.kritz.restfulapi.model.enums.SatuanBahan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BahanDTO {
    private String namaBahan;
    private String deskripsi;
    private String satuanBahan;
    private int jumlahBahan;
    private MultipartFile image;

    public void checkDTO() {
        trim();
        new ImageDTO(image).checkDTO();
        if(this.namaBahan == null) throw new IllegalArgumentException("Nama Bahan Tidak Boleh Bernilai NULL");
        if(this.deskripsi == null) throw new IllegalArgumentException("Deskripsi Tidak Boleh Bernilai NULL");
        if(this.satuanBahan == null) throw new IllegalArgumentException("Satuan Bahan Tidak Boleh Bernilai NULL");
        if(this.jumlahBahan < 0) throw new IllegalArgumentException("Jumlah Bahan Tidak Boleh Bernilai Negatif");
        SatuanBahan.fromString(satuanBahan); //cek valid satuan bahan
    }

    public void trim() {
        this.namaBahan = Optional.ofNullable(this.namaBahan).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.satuanBahan = Optional.ofNullable(this.satuanBahan).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }

}
