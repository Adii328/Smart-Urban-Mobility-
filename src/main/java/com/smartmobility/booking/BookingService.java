package com.smartmobility.booking;

import com.smartmobility.journey.Journey;
import com.smartmobility.journey.JourneyRepository;
import com.smartmobility.user.User;
import com.smartmobility.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final JourneyRepository journeyRepository;

    @Transactional
    public Booking createBooking(Long userId, Long journeyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("Journey not found"));

        Booking booking = Booking.builder()
                .user(user)
                .journey(journey)
                .totalFare(journey.getBaseFare())
                .status("CREATED")
                .paymentStatus("PENDING")
                .createdAt(Instant.now())
                .build();

        // Award initial reward points for booking
        user.setRewardBalance(user.getRewardBalance() + 10.0); // 10 points per booking
        userRepository.save(user);

        return bookingRepository.save(booking);
    }

    public Booking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    public List<Booking> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return bookingRepository.findByUser(user);
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        Booking booking = getBooking(id);
        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking processPayment(Long bookingId, boolean paymentSuccessful) {
        Booking booking = getBooking(bookingId);
        if ("CREATED".equals(booking.getStatus())) {
            if (paymentSuccessful) {
                booking.setPaymentStatus("PAID");
                booking.setStatus("CONFIRMED");
                // Award additional points for successful payment
                User user = booking.getUser();
                user.setRewardBalance(user.getRewardBalance() + 5.0); // 5 bonus points
                userRepository.save(user);
            } else {
                booking.setPaymentStatus("FAILED");
            }
            return bookingRepository.save(booking);
        }
        throw new IllegalStateException("Booking cannot be processed for payment");
    }

    @Transactional
    public Booking applyRewardDiscount(Long bookingId, double pointsToUse) {
        Booking booking = getBooking(bookingId);
        User user = booking.getUser();
        if (user.getRewardBalance() >= pointsToUse) {
            double discount = pointsToUse * 0.1; // 1 point = 0.1 currency unit discount
            booking.setTotalFare(Math.max(0, booking.getTotalFare() - discount));
            user.setRewardBalance(user.getRewardBalance() - pointsToUse);
            userRepository.save(user);
            return bookingRepository.save(booking);
        }
        throw new IllegalArgumentException("Insufficient reward balance");
    }
}

