package com.example.stroop.controller;

import com.example.stroop.model.ColorName;
import com.example.stroop.model.SessionSummary;
import com.example.stroop.model.Stimulus;
import com.example.stroop.model.StimulusResponse;
import com.example.stroop.service.StroopService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final StroopService service;

    public ApiController(StroopService service) {
        this.service = service;
    }

    @GetMapping("/stimulus")
    public Map<String, Object> stimulus(@RequestParam String sessionId) {
        Stimulus s = service.newStimulus(sessionId);
        return Map.of(
                "id", s.id,
                "word", s.word,
                "inkHex", s.ink.hex,
                "issuedAtMs", s.issuedAtMs,
                "choices", ColorName.values()
        );
    }

    @PostMapping("/submit")
    public Map<String, Object> submit(
            @RequestParam String sessionId,
            @Valid @RequestBody StimulusResponse body
    ) {
        boolean correct = service.submit(sessionId, body.stimulusId, body.chosenColor, body.clientLatencyMs);
        return Map.of("correct", correct);
    }

    @GetMapping("/summary")
    public SessionSummary summary(@RequestParam String sessionId) {
        return service.summary(sessionId);
    }
}

