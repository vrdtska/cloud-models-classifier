package com.cloud_models_classifier.model;

public enum CloudModel {
    IAAS("IaaS (Infrastructure as a Service)"),
    PAAS("PaaS (Platform as a Service)"),
    SAAS("SaaS (Software as a Service)"),
    FAAS("FaaS (Function as a Service / Serverless)"),
    UNDETERMINED("Indeterminado / No clasificado");

    private final String displayName;

    CloudModel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}