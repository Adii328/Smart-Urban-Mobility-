package com.smartmobility.journey;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JourneyService {

    private final JourneyRepository journeyRepository;

    // Seed some sample journeys for the assignment (no external APIs)
    @PostConstruct
    public void seedSampleData() {
        if (journeyRepository.count() > 0) {
            return;
        }

        journeyRepository.saveAll(List.of(
                Journey.builder().source("City Center").destination("University").mode("BUS").baseFare(30).durationMinutes(25).sustainable(true).build(),
                Journey.builder().source("City Center").destination("University").mode("METRO").baseFare(40).durationMinutes(15).sustainable(true).build(),
                Journey.builder().source("City Center").destination("Mall").mode("BUS").baseFare(25).durationMinutes(20).sustainable(true).build(),
                Journey.builder().source("City Center").destination("Airport").mode("METRO").baseFare(80).durationMinutes(35).sustainable(true).build(),
                Journey.builder().source("City Center").destination("Airport").mode("TAXI").baseFare(200).durationMinutes(30).sustainable(false).build()
        ));
    }

    public List<Journey> search(String source, String destination) {
        return journeyRepository.findBySourceIgnoreCaseAndDestinationIgnoreCase(source, destination);
    }

    public List<Journey> findAll() {
        return journeyRepository.findAll();
    }
}

