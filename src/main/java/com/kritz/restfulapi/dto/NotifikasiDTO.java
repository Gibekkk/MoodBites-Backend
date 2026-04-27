// package com.kritz.restfulapi.dto;

// import lombok.Setter;

// import com.kritz.restfulapi.model.User;

// import java.util.Optional;

// import com.kritz.restfulapi.model.Broadcast;

// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;

// @Setter
// @Getter
// @NoArgsConstructor
// @AllArgsConstructor
// public class NotifikasiDTO {
//     private String nama;
//     private String deskripsi;
//     private User user;
//     private Broadcast broadcast;

//     public boolean checkDTO() {
//         trim();
//         if (this.nama == null)
//             throw new IllegalArgumentException("Nama Tidak Boleh Bernilai NULL");
//         if (this.deskripsi == null)
//             throw new IllegalArgumentException("Deskripsi Tidak Boleh Bernilai NULL");
//         return this.nama != null && this.deskripsi != null && checkLength();
//     }

//     public boolean checkDTOPesan() {
//         trim();
//         if (this.nama == null)
//             throw new IllegalArgumentException("Nama Tidak Boleh Bernilai NULL");
//         if (this.deskripsi == null)
//             throw new IllegalArgumentException("Deskripsi Tidak Boleh Bernilai NULL");
//         return this.nama != null && this.deskripsi != null && checkLengthPesan();
//     }

//     public boolean checkLengthPesan() {
//         boolean nama = Optional.ofNullable(this.nama).map(s -> s.length() <= 50)
//                 .orElse(true);
//         boolean deskripsi = Optional.ofNullable(this.deskripsi).map(s -> s.length() <= 255)
//                 .orElse(true);
//         if (!nama)
//             throw new IllegalArgumentException("Nama Tidak Valid atau Melewati Batas Karakter");
//         if (!deskripsi)
//             throw new IllegalArgumentException("Deskripsi Tidak Valid atau Melewati Batas Karakter");
//         return nama && deskripsi;
//     }

//     public boolean checkLength() {
//         boolean nama = Optional.ofNullable(this.nama).map(s -> s.length() <= 65535)
//                 .orElse(true);
//         boolean deskripsi = Optional.ofNullable(this.deskripsi).map(s -> s.length() <= 65535)
//                 .orElse(true);
//         if (!nama)
//             throw new IllegalArgumentException("Nama Tidak Valid atau Melewati Batas Karakter");
//         if (!deskripsi)
//             throw new IllegalArgumentException("Deskripsi Tidak Valid atau Melewati Batas Karakter");
//         return nama && deskripsi;
//     }

//     public void trim() {
//         this.nama = Optional.ofNullable(this.nama).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
//         this.deskripsi = Optional.ofNullable(this.deskripsi).map(String::trim).filter(s -> !s.isBlank()).orElse(null);
//     }
// }
