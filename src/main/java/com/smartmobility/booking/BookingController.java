package com.smartmobility.booking;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request.getUserId(), request.getJourneyId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> get(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> userBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Booking> processPayment(@PathVariable Long id, @RequestParam boolean success) {
        return ResponseEntity.ok(bookingService.processPayment(id, success));
    }

    @PostMapping("/{id}/apply-reward")
    public ResponseEntity<Booking> applyReward(@PathVariable Long id, @RequestParam double points) {
        return ResponseEntity.ok(bookingService.applyRewardDiscount(id, points));
    }

    @Data
    private static class CreateBookingRequest {
        private Long userId;
        private Long journeyId;
    }
}

