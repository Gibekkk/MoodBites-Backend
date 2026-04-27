package com.kritz.restfulapi.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.kritz.restfulapi.model.enums.StatusPenjualan;
import com.kritz.restfulapi.model.enums.TipePembayaran;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "penjualan")
public class Penjualan {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_penjualan", nullable = false)
    private StatusPenjualan statusPenjualan;

    @Column(name = "total_bayar", nullable = false)
    private int totalBayar;

    @Column(name = "diskon", nullable = false)
    private double diskon;

    @Column(name = "nama_pelanggan", nullable = true, length = 25)
    private String namaPelanggan;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipe_pembayaran", nullable = true)
    private TipePembayaran tipePembayaran;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_toko", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_penjualanToko"))
    private Toko idToko;

    @OneToMany(mappedBy = "idPenjualan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MenuPenjualan> listMenuPenjualan;

    public int getTotalHarga() {
        int totalHarga = 0;
        for (MenuPenjualan mp : listMenuPenjualan) {
            totalHarga += (mp.getHarga() - (mp.getHarga() * mp.getDiskon() / 100)) * mp.getJumlah();
        }
        totalHarga = (int) (totalHarga - (totalHarga * diskon / 100));
        totalHarga = Math.floorDiv(totalHarga, 100) * 100;
        return totalHarga;
    }
}
