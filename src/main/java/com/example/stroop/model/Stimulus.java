package com.example.stroop.model;

import java.util.UUID;

public class Stimulus {
    public UUID id;
    public String word;        // e.g., "RED"
    public ColorName ink;      // the color to display
    public long issuedAtMs;    // server time

    public Stimulus(UUID id, String word, ColorName ink, long issuedAtMs) {
        this.id = id;
        this.word = word;
        this.ink = ink;
        this.issuedAtMs = issuedAtMs;
    }
}
