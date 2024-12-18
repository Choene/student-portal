package com.example.studentportalapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled;

    @Column(name = "otp_code")
    private String otpCode; // For 2FA OTP

    @Column(name = "otp_expiry")
    private Long otpExpiry; // Timestamp for OTP expiration
}
