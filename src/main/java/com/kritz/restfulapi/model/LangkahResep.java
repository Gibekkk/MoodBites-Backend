package com.kritz.restfulapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "langkah_resep")
public class LangkahResep {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "id_menu", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_langkahResepMenu"))
    private Menu idMenu;

    @Column(name = "deskripsi", nullable = false, columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "urutan", nullable = false)
    private int urutan;
    
}
