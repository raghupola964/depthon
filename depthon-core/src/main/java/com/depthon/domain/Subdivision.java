package com.depthon.domain;

public enum Subdivision {

    // ---------- INFORMATION TECHNOLOGY ----------
    SOFTWARE_DEVELOPER("Software Developer", Division.INFORMATION_TECHNOLOGY),
    DATA_ANALYST("Data Analyst", Division.INFORMATION_TECHNOLOGY),
    DATA_ENGINEER("Data Engineer", Division.INFORMATION_TECHNOLOGY),
    CLOUD_ENGINEER("Cloud Engineer", Division.INFORMATION_TECHNOLOGY),
    DEVOPS_ENGINEER("DevOps Engineer", Division.INFORMATION_TECHNOLOGY),
    CYBERSECURITY_ANALYST("Cybersecurity Analyst", Division.INFORMATION_TECHNOLOGY),
    QA_TEST_ENGINEER("QA / Test Engineer", Division.INFORMATION_TECHNOLOGY),
    SYSTEMS_ADMINISTRATOR("Systems Administrator", Division.INFORMATION_TECHNOLOGY),
    NETWORK_ENGINEER("Network Engineer", Division.INFORMATION_TECHNOLOGY),
    IT_SUPPORT_SPECIALIST("IT Support Specialist", Division.INFORMATION_TECHNOLOGY),

    // ---------- ARTIFICIAL INTELLIGENCE ----------
    MACHINE_LEARNING_ENGINEER("Machine Learning Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    DATA_SCIENTIST("Data Scientist", Division.ARTIFICIAL_INTELLIGENCE),
    AI_RESEARCHER("AI Researcher", Division.ARTIFICIAL_INTELLIGENCE),
    MLOPS_ENGINEER("MLOps Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    NLP_ENGINEER("NLP Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    COMPUTER_VISION_ENGINEER("Computer Vision Engineer", Division.ARTIFICIAL_INTELLIGENCE),
    AI_PRODUCT_MANAGER("AI Product Manager", Division.ARTIFICIAL_INTELLIGENCE),
    PROMPT_ENGINEER("Prompt Engineer", Division.ARTIFICIAL_INTELLIGENCE),

    // ---------- HEALTHCARE ----------
    PHYSICIAN("Physician", Division.HEALTHCARE),
    REGISTERED_NURSE("Registered Nurse", Division.HEALTHCARE),
    PHARMACIST("Pharmacist", Division.HEALTHCARE),
    PHYSICAL_THERAPIST("Physical Therapist", Division.HEALTHCARE),
    MEDICAL_LAB_TECHNICIAN("Medical Lab Technician", Division.HEALTHCARE),
    RADIOLOGIC_TECHNOLOGIST("Radiologic Technologist", Division.HEALTHCARE),
    HEALTHCARE_ADMINISTRATOR("Healthcare Administrator", Division.HEALTHCARE),
    MENTAL_HEALTH_COUNSELOR("Mental Health Counselor", Division.HEALTHCARE),

    // ---------- FINANCE ----------
    FINANCIAL_ANALYST("Financial Analyst", Division.FINANCE),
    INVESTMENT_BANKER("Investment Banker", Division.FINANCE),
    ACCOUNTANT("Accountant", Division.FINANCE),
    FINANCIAL_ADVISOR("Financial Advisor", Division.FINANCE),
    AUDITOR("Auditor", Division.FINANCE),
    PORTFOLIO_MANAGER("Portfolio Manager", Division.FINANCE),
    RISK_ANALYST("Risk Analyst", Division.FINANCE),
    TAX_SPECIALIST("Tax Specialist", Division.FINANCE),

    // ---------- BUSINESS & MANAGEMENT ----------
    PRODUCT_MANAGER("Product Manager", Division.BUSINESS_AND_MANAGEMENT),
    PROJECT_MANAGER("Project Manager", Division.BUSINESS_AND_MANAGEMENT),
    BUSINESS_ANALYST("Business Analyst", Division.BUSINESS_AND_MANAGEMENT),
    OPERATIONS_MANAGER("Operations Manager", Division.BUSINESS_AND_MANAGEMENT),
    MANAGEMENT_CONSULTANT("Management Consultant", Division.BUSINESS_AND_MANAGEMENT),
    HUMAN_RESOURCES_MANAGER("Human Resources Manager", Division.BUSINESS_AND_MANAGEMENT),
    SUPPLY_CHAIN_MANAGER("Supply Chain Manager", Division.BUSINESS_AND_MANAGEMENT),
    ENTREPRENEUR("Entrepreneur / Founder", Division.BUSINESS_AND_MANAGEMENT),

    // ---------- ENGINEERING ----------
    MECHANICAL_ENGINEER("Mechanical Engineer", Division.ENGINEERING),
    ELECTRICAL_ENGINEER("Electrical Engineer", Division.ENGINEERING),
    CIVIL_ENGINEER("Civil Engineer", Division.ENGINEERING),
    CHEMICAL_ENGINEER("Chemical Engineer", Division.ENGINEERING),
    AEROSPACE_ENGINEER("Aerospace Engineer", Division.ENGINEERING),
    INDUSTRIAL_ENGINEER("Industrial Engineer", Division.ENGINEERING),
    BIOMEDICAL_ENGINEER("Biomedical Engineer", Division.ENGINEERING),
    ENVIRONMENTAL_ENGINEER("Environmental Engineer", Division.ENGINEERING),

    // ---------- ARCHITECTURE & CONSTRUCTION ----------
    ARCHITECT("Architect", Division.ARCHITECTURE_AND_CONSTRUCTION),
    STRUCTURAL_ENGINEER("Structural Engineer", Division.ARCHITECTURE_AND_CONSTRUCTION),
    URBAN_PLANNER("Urban Planner", Division.ARCHITECTURE_AND_CONSTRUCTION),
    CONSTRUCTION_MANAGER("Construction Manager", Division.ARCHITECTURE_AND_CONSTRUCTION),
    INTERIOR_DESIGNER("Interior Designer", Division.ARCHITECTURE_AND_CONSTRUCTION),
    LANDSCAPE_ARCHITECT("Landscape Architect", Division.ARCHITECTURE_AND_CONSTRUCTION),
    QUANTITY_SURVEYOR("Quantity Surveyor", Division.ARCHITECTURE_AND_CONSTRUCTION),

    // ---------- MARKETING & SALES ----------
    DIGITAL_MARKETER("Digital Marketer", Division.MARKETING_AND_SALES),
    SALES_MANAGER("Sales Manager", Division.MARKETING_AND_SALES),
    CONTENT_STRATEGIST("Content Strategist", Division.MARKETING_AND_SALES),
    SEO_SPECIALIST("SEO Specialist", Division.MARKETING_AND_SALES),
    BRAND_MANAGER("Brand Manager", Division.MARKETING_AND_SALES),
    SOCIAL_MEDIA_MANAGER("Social Media Manager", Division.MARKETING_AND_SALES),
    ACCOUNT_EXECUTIVE("Account Executive", Division.MARKETING_AND_SALES),
    MARKETING_ANALYST("Marketing Analyst", Division.MARKETING_AND_SALES),

    // ---------- EDUCATION ----------
    TEACHER("Teacher", Division.EDUCATION),
    PROFESSOR("Professor", Division.EDUCATION),
    INSTRUCTIONAL_DESIGNER("Instructional Designer", Division.EDUCATION),
    ACADEMIC_RESEARCHER("Academic Researcher", Division.EDUCATION),
    EDUCATION_ADMINISTRATOR("Education Administrator", Division.EDUCATION),
    SCHOOL_COUNSELOR("School Counselor", Division.EDUCATION),
    CURRICULUM_DEVELOPER("Curriculum Developer", Division.EDUCATION),

    // ---------- DESIGN & CREATIVE ----------
    UX_DESIGNER("UX Designer", Division.DESIGN_AND_CREATIVE),
    GRAPHIC_DESIGNER("Graphic Designer", Division.DESIGN_AND_CREATIVE),
    PRODUCT_DESIGNER("Product Designer", Division.DESIGN_AND_CREATIVE),
    MOTION_DESIGNER("Motion Designer", Division.DESIGN_AND_CREATIVE),
    ILLUSTRATOR("Illustrator", Division.DESIGN_AND_CREATIVE),
    UI_DESIGNER("UI Designer", Division.DESIGN_AND_CREATIVE),
    UX_RESEARCHER("UX Researcher", Division.DESIGN_AND_CREATIVE),
    CONTENT_WRITER("Content Writer", Division.DESIGN_AND_CREATIVE);

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