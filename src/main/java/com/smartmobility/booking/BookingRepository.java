package com.smartmobility.booking;

import com.smartmobility.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    @Query("select coalesce(sum(b.totalFare), 0) from Booking b where b.status = 'CONFIRMED'")
    Double sumConfirmedRevenue();
}

