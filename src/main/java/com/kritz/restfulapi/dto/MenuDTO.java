package com.kritz.restfulapi.dto;

import lombok.Setter;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.kritz.restfulapi.dto.lists.BahanList;
import com.kritz.restfulapi.model.enums.Kategori;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {
    private String nama;
    private String kategori;
    private String deskripsi;
    private int harga;
    private List<BahanList> listBahan;
    private List<LangkahDTO> listLangkah;
    private MultipartFile image;

    public void checkDTO() {
        trim();
        checkLength();
        new ImageDTO(image).checkDTO();
        if (this.nama == null)
            throw new IllegalArgumentException("Nama Tidak Boleh Bernilai NULL");
        if (this.kategori == null)
            throw new IllegalArgumentException("Kategori Tidak Boleh Bernilai NULL");
        if (this.deskripsi == null)
            throw new IllegalArgumentException("Deskripsi Tidak Boleh Bernilai NULL");
        if (this.listBahan == null || this.listBahan.isEmpty())
            throw new IllegalArgumentException("List Bahan Tidak Boleh Bernilai NULL atau Kosong");
        if (this.listLangkah == null || this.listLangkah.isEmpty())
            throw new IllegalArgumentException("List Langkah Tidak Boleh Bernilai NULL atau Kosong");
        if (this.harga <= 0)
            throw new IllegalArgumentException("Harga Tidak Boleh Kurang Dari Sama Dengan 0");
        Kategori.fromString(kategori); //cek valid kategori
        for (BahanList bahanList : listBahan) {
            bahanList.setIdBahan(bahanList.getIdBahan().trim());
            bahanList.checkDTO();
        }
        for (LangkahDTO langkahDTO : listLangkah) {
            langkahDTO.setDeskripsi(langkahDTO.getDeskripsi().trim());
            langkahDTO.checkDTO();
        }
    }

    public void checkLength() {
        boolean nama = Optional.ofNullable(this.nama)
                .map(s -> s.length() <= 100)
                .orElse(true);
        boolean kategori = Optional.ofNullable(this.kategori)
                .map(s -> s.length() <= 50)
                .orElse(true);
        boolean deskripsi = Optional.ofNullable(this.deskripsi)
                .map(s -> s.length() <= 500)
                .orElse(true);
        if (!deskripsi)
            throw new IllegalArgumentException("Deskripsi Tidak Valid atau Melewati Batas Karakter");
        if (!kategori)
            throw new IllegalArgumentException("Kategori Tidak Valid atau Melewati Batas Karakter");
        if (!nama)
            throw new IllegalArgumentException("Nama Tidak Valid atau Melewati Batas Karakter");
    }

    public void trim() {
        this.nama = Optional.ofNullable(this.nama).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.kategori = Optional.ofNullable(this.kategori).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
        this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
    }
}
