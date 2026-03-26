package com.smartmobility.admin;

import com.smartmobility.booking.Booking;
import com.smartmobility.booking.BookingRepository;
import com.smartmobility.journey.Journey;
import com.smartmobility.journey.JourneyRepository;
import com.smartmobility.user.User;
import com.smartmobility.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final JourneyRepository journeyRepository;

    @GetMapping("/summary")
    public ResponseEntity<AdminSummary> summary() {
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.findAll().stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .count();
        Double revenue = bookingRepository.sumConfirmedRevenue();
        Double totalRewards = userRepository.sumAllRewardBalances();

        AdminSummary dto = new AdminSummary(
                totalUsers,
                totalBookings,
                confirmedBookings,
                revenue != null ? revenue : 0.0,
                totalRewards != null ? totalRewards : 0.0
        );
        return ResponseEntity.ok(dto);
    }

    // User CRUD
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(userDetails.getEmail());
                    user.setFullName(userDetails.getFullName());
                    user.setRole(userDetails.getRole());
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Booking CRUD
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id) {
        return bookingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/bookings/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        return bookingRepository.findById(id)
                .map(booking -> {
                    booking.setStatus(bookingDetails.getStatus());
                    return ResponseEntity.ok(bookingRepository.save(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Journey CRUD
    @GetMapping("/journeys")
    public ResponseEntity<List<Journey>> getAllJourneys() {
        return ResponseEntity.ok(journeyRepository.findAll());
    }

    @GetMapping("/journeys/{id}")
    public ResponseEntity<Journey> getJourney(@PathVariable Long id) {
        return journeyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/journeys")
    public ResponseEntity<Journey> createJourney(@RequestBody Journey journey) {
        return ResponseEntity.ok(journeyRepository.save(journey));
    }

    @PutMapping("/journeys/{id}")
    public ResponseEntity<Journey> updateJourney(@PathVariable Long id, @RequestBody Journey journeyDetails) {
        return journeyRepository.findById(id)
                .map(journey -> {
                    journey.setSource(journeyDetails.getSource());
                    journey.setDestination(journeyDetails.getDestination());
                    journey.setMode(journeyDetails.getMode());
                    journey.setBaseFare(journeyDetails.getBaseFare());
                    journey.setDurationMinutes(journeyDetails.getDurationMinutes());
                    journey.setSustainable(journeyDetails.isSustainable());
                    return ResponseEntity.ok(journeyRepository.save(journey));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/journeys/{id}")
    public ResponseEntity<Void> deleteJourney(@PathVariable Long id) {
        if (journeyRepository.existsById(id)) {
            journeyRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}

