package com.example.stroop.service;

import com.example.stroop.model.ColorName;
import com.example.stroop.model.SessionSummary;
import com.example.stroop.model.Stimulus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class StroopService {
    private final SecureRandom random = new SecureRandom();
    private final List<ColorName> palette = List.of(
            ColorName.RED, ColorName.GREEN, ColorName.BLUE,
            ColorName.YELLOW, ColorName.PURPLE, ColorName.ORANGE
    );

    // In-memory per-session storage
    private final Map<String, Map<UUID, Stimulus>> sessionStimuli = new ConcurrentHashMap<>();
    private final Map<String, List<Result>> sessionResults = new ConcurrentHashMap<>();

    private static class Result {
        boolean correct; long latencyMs;
        Result(boolean c, long ms) { this.correct = c; this.latencyMs = ms; }
    }

    public Stimulus newStimulus(String sessionId) {
        var wordColor = palette.get(random.nextInt(palette.size()));
        var inkColor  = palette.get(random.nextInt(palette.size()));
        // ~40% congruent (60% incongruent)
        if (random.nextDouble() < 0.4) {
            inkColor = wordColor;
        }
        var stim = new Stimulus(UUID.randomUUID(), wordColor.name(), inkColor, System.currentTimeMillis());
        sessionStimuli.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>()).put(stim.id, stim);
        return stim;
    }

    public boolean submit(String sessionId, UUID id, ColorName chosenColor, Long clientLatencyMs) {
        var stimMap = sessionStimuli.getOrDefault(sessionId, Map.of());
        var stim = stimMap.get(id);
        if (stim == null) return false;

        boolean ok = chosenColor == stim.ink;
        long latency = clientLatencyMs == null ? 0L : clientLatencyMs;
        sessionResults.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(new Result(ok, latency));
        // prevent resubmits
        stimMap.remove(id);
        return ok;
    }

    public SessionSummary summary(String sessionId) {
        var list = sessionResults.getOrDefault(sessionId, List.of());
        int total = list.size();
        int correct = (int) list.stream().filter(r -> r.correct).count();
        long avgLatency = 0L;
        if (total > 0) {
            var latencies = list.stream().mapToLong(r -> r.latencyMs).filter(v -> v > 0).toArray();
            if (latencies.length > 0) {
                avgLatency = Math.round(Arrays.stream(latencies).average().orElse(0));
            }
        }
        double accuracy = total == 0 ? 0.0 : (correct * 100.0 / total);
        return new SessionSummary(total, correct, accuracy, avgLatency);
    }
}
