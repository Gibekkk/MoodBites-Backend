package com.kritz.restfulapi.dto;

import lombok.Setter;

import java.util.List;
import com.kritz.restfulapi.dto.lists.BahanList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestockDTO {
    private List<BahanList> listBahan;

    public void checkDTO() {
        if (this.listBahan == null || this.listBahan.isEmpty())
            throw new IllegalArgumentException("List Bahan Tidak Boleh Bernilai NULL atau Kosong");
        for (BahanList bahanList : listBahan) {
            bahanList.setIdBahan(bahanList.getIdBahan().trim());
            bahanList.checkDTO();
        }
    }
}
