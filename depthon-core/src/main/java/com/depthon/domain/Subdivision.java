package com.depthon.domain;

public enum Subdivision {
    // Information Technology
    SOFTWARE_DEVELOPER("Software Developer", Division.INFORMATION_TECHNOLOGY),
    DATA_ANALYST("Data Analyst", Division.INFORMATION_TECHNOLOGY),
    DATA_ENGINEER("Data Engineer", Division.INFORMATION_TECHNOLOGY),
    CLOUD_ENGINEER("Cloud Engineer", Division.INFORMATION_TECHNOLOGY),
    DEVOPS_ENGINEER("DevOps Engineer", Division.INFORMATION_TECHNOLOGY),
    CYBERSECURITY("Cybersecurity", Division.INFORMATION_TECHNOLOGY),
    QA_TEST_ENGINEER("QA / Test Engineer", Division.INFORMATION_TECHNOLOGY),

    // Artificial Intelligence
    MACHINE_LEARNING_ENGINEER("Machine Learning Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    DATA_SCIENTIST("Data Scientist", Division.ARTIFICIAL_INTELLIGENCE),
    AI_RESEARCHER("AI Researcher", Division.ARTIFICIAL_INTELLIGENCE),
    MLOPS_ENGINEER("MLOps Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    NLP_ENGINEER("NLP Engineer", Division.ARTIFICIAL_INTELLIGENCE);

    private final String displayName;
    private final Division division;

    Subdivision(String displayName, Division division) {
        this.displayName = displayName;
        this.division = division;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Division getDivision() {
        return division;
    }
}