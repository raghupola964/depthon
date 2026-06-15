// The profession taxonomy for the signup dropdowns.
// Keys = divisions (the enum names the backend expects).
// Values = the subdivisions under each (also enum names).

export const TAXONOMY = {
  INFORMATION_TECHNOLOGY: {
    label: "Information Technology",
    subdivisions: [
      { value: "SOFTWARE_DEVELOPER", label: "Software Developer" },
      { value: "DATA_ANALYST", label: "Data Analyst" },
      { value: "DATA_ENGINEER", label: "Data Engineer" },
      { value: "CLOUD_ENGINEER", label: "Cloud Engineer" },
      { value: "DEVOPS_ENGINEER", label: "DevOps Engineer" },
      { value: "CYBERSECURITY", label: "Cybersecurity" },
      { value: "QA_TEST_ENGINEER", label: "QA / Test Engineer" },
    ],
  },
  ARTIFICIAL_INTELLIGENCE: {
    label: "Artificial Intelligence",
    subdivisions: [
      { value: "MACHINE_LEARNING_ENGINEER", label: "Machine Learning Engineer" },
      { value: "DATA_SCIENTIST", label: "Data Scientist" },
      { value: "AI_RESEARCHER", label: "AI Researcher" },
      { value: "MLOPS_ENGINEER", label: "MLOps Engineer" },
      { value: "NLP_ENGINEER", label: "NLP Engineer" },
    ],
  },
};