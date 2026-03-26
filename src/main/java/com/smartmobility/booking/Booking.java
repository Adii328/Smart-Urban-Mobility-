package com.smartmobility.booking;

import com.smartmobility.journey.Journey;
import com.smartmobility.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Journey journey;

    @Column(nullable = false)
    private double totalFare;

    @Column(nullable = false)
    private String status; // CREATED, CONFIRMED, CANCELLED

    @Column(nullable = false)
    private String paymentStatus; // PENDING, PAID, FAILED

    @Column(nullable = false)
    private Instant createdAt;
}

