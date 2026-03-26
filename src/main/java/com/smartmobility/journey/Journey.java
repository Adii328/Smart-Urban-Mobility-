package com.smartmobility.journey;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "journeys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String mode; // BUS, METRO, SCOOTER, WALK

    @Column(nullable = false)
    private double baseFare;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private boolean sustainable; // true for eco-friendly modes
}

