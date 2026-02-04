package com.example.stroop;

import com.example.stroop.service.StroopService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StroopServiceTests {

    @Test
    void generatesStimulus() {
        var svc = new StroopService();
        var s = svc.newStimulus("demo");
        assertNotNull(s.id);
        assertNotNull(s.word);
        assertNotNull(s.ink);
    }

    @Test
    void submitAndSummary() {
        var svc = new StroopService();
        var s = svc.newStimulus("demo");
        boolean ok = svc.submit("demo", s.id, s.ink, 420L);
        assertTrue(ok);
        var sum = svc.summary("demo");
        assertEquals(1, sum.totalTrials);
        assertEquals(1, sum.correct);
        assertTrue(sum.avgLatencyMs >= 0);
    }
}

