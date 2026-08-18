package com.cloud_models_classifier.model;

public enum CloudModel {
    IAAS("IaaS", "IaaS (Infrastructure as a Service)"),
    PAAS("PaaS", "PaaS (Platform as a Service)"),
    SAAS("SaaS", "SaaS (Software as a Service)"),
    FAAS("FaaS", "FaaS (Function as a Service / Serverless)"),
    UNDETERMINED("Indeterminado", "Indeterminado / No clasificado");

    private final String acronym;
    private final String displayName;

    CloudModel(String acronym, String displayName) {
        this.acronym = acronym;
        this.displayName = displayName;
    }

    public String getAcronym() { return acronym; }
    public String getDisplayName() { return displayName; }
}