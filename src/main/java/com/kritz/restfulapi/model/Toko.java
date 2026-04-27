package com.kritz.restfulapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "toko")
public class Toko {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_login", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_tokoLogin"))
    private Login idLogin;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "deskripsi", nullable = true, columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @OneToMany(mappedBy = "idToko", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Penjualan> listPenjualan;

    @OneToMany(mappedBy = "idToko", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Menu> listMenu;

    @OneToMany(mappedBy = "idToko", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Bahan> listBahan;

    @OneToMany(mappedBy = "idToko", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PengisianStock> listPengisianStock;

}
