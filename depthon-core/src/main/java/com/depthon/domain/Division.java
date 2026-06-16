package com.depthon.domain;

public enum Division {
    INFORMATION_TECHNOLOGY("Information Technology"),
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    HEALTHCARE("Healthcare"),
    FINANCE("Finance"),
    BUSINESS_AND_MANAGEMENT("Business & Management"),
    ENGINEERING("Engineering"),
    ARCHITECTURE_AND_CONSTRUCTION("Architecture & Construction"),
    MARKETING_AND_SALES("Marketing & Sales"),
    EDUCATION("Education"),
    DESIGN_AND_CREATIVE("Design & Creative");

    private final String displayName;

    Division(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}