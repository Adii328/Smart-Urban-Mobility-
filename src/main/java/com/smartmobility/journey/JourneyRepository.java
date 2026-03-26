package com.smartmobility.journey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JourneyRepository extends JpaRepository<Journey, Long> {

    List<Journey> findBySourceIgnoreCaseAndDestinationIgnoreCase(String source, String destination);
}

