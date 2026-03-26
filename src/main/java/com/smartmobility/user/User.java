package com.smartmobility.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String role; // e.g. USER, ADMIN, CITY_AUTHORITY

    @Column(nullable = false)
    private double rewardBalance = 0.0; // Reward points balance

    private String preferredMode; // e.g. METRO, BUS, SCOOTER

    private String homeLocation;

    private String workLocation;
}

