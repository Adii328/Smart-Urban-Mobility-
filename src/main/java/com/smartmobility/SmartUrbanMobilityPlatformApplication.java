package com.smartmobility;

import com.smartmobility.journey.Journey;
import com.smartmobility.journey.JourneyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartUrbanMobilityPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartUrbanMobilityPlatformApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(JourneyRepository journeyRepository) {
        return args -> {
            if (journeyRepository.count() == 0) {
                journeyRepository.save(new Journey(null, "City Center", "University", "Bus", 50.0, 30, true));
                journeyRepository.save(new Journey(null, "University", "City Center", "Bus", 50.0, 30, true));
                journeyRepository.save(new Journey(null, "City Center", "Airport", "Train", 100.0, 45, true));
                journeyRepository.save(new Journey(null, "Airport", "City Center", "Train", 100.0, 45, true));
                journeyRepository.save(new Journey(null, "University", "Airport", "Taxi", 80.0, 60, false));
            }
        };
    }
}

