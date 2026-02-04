package com.example.stroop.model;

public enum ColorName {
    RED("#e53935"),
    GREEN("#43a047"),
    BLUE("#1e88e5"),
    YELLOW("#fdd835"),
    PURPLE("#8e24aa"),
    ORANGE("#fb8c00");

    public final String hex;
    ColorName(String hex) { this.hex = hex; }
}

