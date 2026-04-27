package com.kritz.restfulapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.kritz.restfulapi.model.enums.Kategori;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu")
public class Menu {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_toko", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_produkToko"))
    private Toko idToko;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "deskripsi", nullable = false, length = 255)
    private String deskripsi;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori", nullable = false)
    private Kategori kategori;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @OneToOne(mappedBy = "idMenu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pricelist idPricelist;

    @OneToMany(mappedBy = "idMenu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LangkahResep> listLangkahResep;

    @OneToMany(mappedBy = "idMenu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<BahanResep> listBahanResep;

    @OneToMany(mappedBy = "idMenu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MenuPenjualan> listMenuPenjualan;

}
