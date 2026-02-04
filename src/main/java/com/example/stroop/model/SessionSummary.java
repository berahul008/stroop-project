package com.example.stroop.model;

public class SessionSummary {
    public int totalTrials;
    public int correct;
    public double accuracy;   // percentage 0..100
    public long avgLatencyMs;

    public SessionSummary(int totalTrials, int correct, double accuracy, long avgLatencyMs) {
        this.totalTrials = totalTrials;
        this.correct = correct;
        this.accuracy = accuracy;
        this.avgLatencyMs = avgLatencyMs;
    }
}
