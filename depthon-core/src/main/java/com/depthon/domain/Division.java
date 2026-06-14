package com.depthon.domain;

public enum Division {
    INFORMATION_TECHNOLOGY("Information Technology"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence");

    private final String displayName;

    Division(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}