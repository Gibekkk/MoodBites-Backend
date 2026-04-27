package com.kritz.restfulapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "menu_penjualan")
public class MenuPenjualan {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_penjualan", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_produkPenjualanPenjualan"))
    private Penjualan idPenjualan;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_menu", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_produkPenjualanMenu"))
    private Menu idMenu;

    @Column(name = "harga", nullable = false)
    private int harga;

    @Column(name = "diskon", nullable = false)
    private int diskon;

    @Column(name = "jumlah", nullable = false)
    private int jumlah;

    @Column(name = "komentar", nullable = true, columnDefinition = "TEXT")
    private String komentar;

}
