package com.kritz.restfulapi.model;

import java.time.LocalDateTime;

import com.kritz.restfulapi.model.enums.OTPType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp")
public class OTP {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(nullable = false, name = "id_login", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_otpLogin"))
    private Login idLogin;

    @Column(name = "kode", nullable = false, length = 6)
    private String kode;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false)
    private OTPType otpType;

    @Column(name = "valid_until", nullable = true)
    private LocalDateTime validUntil;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "edited_at", nullable = false)
    private LocalDateTime editedAt;

}
