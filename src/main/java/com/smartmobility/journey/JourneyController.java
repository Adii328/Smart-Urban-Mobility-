package com.smartmobility.journey;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
@CrossOrigin
public class JourneyController {

    private final JourneyService journeyService;

    @GetMapping("/all")
    public ResponseEntity<List<Journey>> all() {
        return ResponseEntity.ok(journeyService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Journey>> search(
            @RequestParam String source,
            @RequestParam String destination
    ) {
        return ResponseEntity.ok(journeyService.search(source, destination));
    }
}

