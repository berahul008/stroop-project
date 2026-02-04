package com.example.stroop.model;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class StimulusResponse {
    @NotNull public UUID stimulusId;
    @NotNull public ColorName chosenColor;
    public Long clientLatencyMs;

    public StimulusResponse() {}
}

