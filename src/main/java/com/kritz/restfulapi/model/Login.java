package com.kritz.restfulapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.kritz.restfulapi.model.enums.Level;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "login")
public class Login {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

    @Column(name = "verified_at", nullable = true)
    private LocalDateTime verifiedAt;

    @OneToOne(mappedBy = "idLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Toko idToko;

    @OneToOne(mappedBy = "idLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private OTP otp;

    @OneToMany(mappedBy = "idLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Notifikasi> listNotifikasi;

    @OneToMany(mappedBy = "idLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Session> listSession;

}
